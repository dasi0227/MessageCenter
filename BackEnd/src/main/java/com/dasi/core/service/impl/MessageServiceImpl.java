package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.constant.RedisConstant;
import com.dasi.common.constant.SendConstant;
import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.common.exception.SendException;
import com.dasi.common.properties.RabbitMqProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.channel.DlxSender;
import com.dasi.core.mapper.MessageMapper;
import com.dasi.core.service.*;
import com.dasi.pojo.dto.DispatchPageDTO;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.dto.PromptDTO;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
import com.dasi.util.AiClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    @Autowired
    private ContactService contactService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @Autowired
    private DlxSender dlxSender;

    @Autowired
    private AiClientUtil aiClientUtil;

    @Override
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisConstant.CACHE_MESSAGE_PREFIX, allEntries = true),
            @CacheEvict(value = RedisConstant.CACHE_DASHBOARD_PREFIX, allEntries = true)
    })
    public void sendMessage(MessageSendDTO dto) {
        // 构建消息主体
        Long accountId = AccountContextHolder.get().getId();
        String accountName = AccountContextHolder.get().getName();

        Message message = BeanUtil.copyProperties(dto, Message.class);
        message.setAccountId(accountId);
        message.setAccountName(accountName);
        save(message);

        // 查询收件人信息
        List<Contact> contactList = contactService.listByIds(dto.getContactIds());

        // 构建分发主体
        List<Dispatch> dispatchList = new ArrayList<>();
        List<Dispatch> sendList = new ArrayList<>();
        for (Contact contact : contactList) {
            Dispatch dispatch = Dispatch.builder()
                    .messageId(message.getId())
                    .subject(dto.getSubject())
                    .content(dto.getContent())
                    .attachments(dto.getAttachments())
                    .departmentId(dto.getDepartmentId())
                    .departmentName(dto.getDepartmentName())
                    .contactId(contact.getId())
                    .contactName(contact.getName())
                    .status(MsgStatus.PENDING)
                    .errorMsg(null)
                    .createdAt(LocalDateTime.now())
                    .build();

            try {
                checkScheduleAt(dto.getScheduleAt());
                dispatch.setTarget(contactService.resolveTarget(contact.getId(), dto.getChannel()));
                contactService.check(dispatch);
                sensitiveWordService.detect(dispatch);
                renderService.decode(dispatch);
            } catch (Exception exception) {
                dispatch.setStatus(MsgStatus.FAIL);
                dispatch.setErrorMsg(exception.getMessage());
            }

            dispatchList.add(dispatch);
            if (dispatch.getStatus() != MsgStatus.FAIL) {
                sendList.add(dispatch);
            }
        }
        dispatchService.saveBatch(dispatchList);

        // MQ 参数解析
        String exchange = rabbitMqProperties.getExchange();
        String route = dto.getChannel().getRoute(rabbitMqProperties);
        long delayMillis = dto.getScheduleAt() == null ? 0L : ChronoUnit.MILLIS.between(LocalDateTime.now(), dto.getScheduleAt());

        // 发送到消息队列
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (Dispatch dispatch : sendList) {
                    try {
                        rabbitTemplate.convertAndSend(exchange, route, dispatch, processor -> {
                            processor.getMessageProperties().setDelayLong(delayMillis);
                            return processor;
                        });
                        dispatchService.updateStatus(dispatch, MsgStatus.SENDING, null);
                    } catch (Exception exception) {
                        String errorMsg = SendConstant.SEND_MAILBOX_FAIL + exception.getMessage();
                        dispatchService.updateStatus(dispatch, MsgStatus.ERROR, errorMsg);
                        dlxSender.sendDlx(dispatch, exception);
                    }
                }
            }
        });
    }

    private void checkScheduleAt(LocalDateTime scheduleAt) {
        if (scheduleAt == null) {
            return;
        }
        if (scheduleAt.isBefore(LocalDateTime.now())) {
            throw new SendException(SendConstant.SCHEDULE_BEFORE_NOW);
        }
        if (ChronoUnit.DAYS.between(LocalDateTime.now(), scheduleAt) > 31) {
            throw new SendException(SendConstant.SCHEDULE_AFTER_MONTH);
        }
    }

    @Override
    @Cacheable(
            value = RedisConstant.CACHE_MESSAGE_PREFIX,
            key = "'page:' + #dto.pageNum",
            condition = "#dto.pure"
    )
    public PageResult<Message> getMessagePage(MessagePageDTO dto) {
        Page<Message> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                // 精确查询
                .eq(dto.getChannel() != null, Message::getChannel, dto.getChannel())
                .eq(dto.getAccountId() != null, Message::getAccountId, dto.getAccountId())
                .eq(dto.getDepartmentId() != null, Message::getDepartmentId, dto.getDepartmentId())
                // 模糊查询
                .like(StrUtil.isNotBlank(dto.getDepartmentName()), Message::getDepartmentName, dto.getDepartmentName())
                .like(StrUtil.isNotBlank(dto.getAccountName()), Message::getAccountName, dto.getAccountName())
                .like(StrUtil.isNotBlank(dto.getSubject()), Message::getSubject, dto.getSubject())
                .like(StrUtil.isNotBlank(dto.getContent()), Message::getContent, dto.getContent())
                // 时间查询
                .ge(dto.getStartTime() != null, Message::getCreatedAt, dto.getStartTime())
                .le(dto.getEndTime() != null, Message::getCreatedAt, dto.getEndTime())
                // 排序
                .orderByDesc(Message::getCreatedAt);

        Page<Message> result = page(param, wrapper);
        return PageResult.of(result);
    }

    @Override
    @Cacheable(
            value = RedisConstant.CACHE_DISPATCH_PREFIX,
            key = "#dto.messageId + ':page:' + #dto.pageNum",
            condition = "#dto.pure"
    )
    public PageResult<Dispatch> getMessageDetail(DispatchPageDTO dto) {
        Page<Dispatch> param = new Page<>(dto.getPageNum(), dto.getPageSize());

        LambdaQueryWrapper<Dispatch> wrapper = new LambdaQueryWrapper<Dispatch>()
                // 外键
                .eq(dto.getMessageId() != null, Dispatch::getMessageId, dto.getMessageId())
                // 精确查询
                .eq(dto.getStatus() != null, Dispatch::getStatus, dto.getStatus())
                .eq(dto.getContactId() != null, Dispatch::getContactId, dto.getContactId())
                // 模糊查询
                .like(StrUtil.isNotBlank(dto.getContactName()), Dispatch::getContactName, dto.getContactName())
                .like(StrUtil.isNotBlank(dto.getSubject()), Dispatch::getSubject, dto.getSubject())
                .like(StrUtil.isNotBlank(dto.getContent()), Dispatch::getContent, dto.getContent())
                .orderByDesc(Dispatch::getCreatedAt);

        Page<Dispatch> result = dispatchService.page(param, wrapper);

        return PageResult.of(result);
    }

    @Override
    public String getLlmMessage(PromptDTO dto) {

        String systemPrompt = """
            你是一名企业级「消息内容生成助手」，服务对象为内部消息中台（MessageCenter）系统。
            你的任务是根据用户提供的业务需求生成可直接发送的企业消息文本。

            【输出规则】
            1. 不输出标题，直接从正文开始。
            2. 内容必须正式、清晰、简洁，符合企业沟通规范。
            3. 不使用 Emoji、不使用口语化表达、不使用 Markdown、不使用代码块、不使用 JSON。
            4. 不出现任何 AI 身份描述、道歉语句、推测性内容或系统信息。
            5. 内容应结构合理，有自然段落。
            6. 若用户提供时间、地点、事件、要求等信息，你必须补全为逻辑完整的企业文本。
            7. 若用户输入模糊，你应基于企业场景进行合理补全，但不能编造无关信息。
            8. 输出必须是纯文本，适合直接存入数据库并投递给联系人。

            【场景增强要求】
            - 流程通知需包含步骤说明。
            - 异常/告警需语气严谨并包含行动要求。
            - 日常消息自然礼貌。
            - 附件如存在，可自然说明“相关资料已随邮件附上”。
            """;

        String userPrompt = """
            请根据以下用户需求生成一条可直接发送的消息内容：

            【用户需求】
            %s

            请直接给出最终内容。
            """.formatted(dto.getPrompt());

        return aiClientUtil.call(dto.getModel(), systemPrompt, userPrompt);
    }

    @Override
    @Cacheable(value = RedisConstant.CACHE_MESSAGE_PREFIX, key = "'entity:' + '#id'")
    public Message getMessage(Long id) {
        Message message = getById(id);

        if (message == null) {
            throw new MessageCenterException(ResultInfo.MESSAGE_NOT_FOUND);
        }

        return message;
    }

}
