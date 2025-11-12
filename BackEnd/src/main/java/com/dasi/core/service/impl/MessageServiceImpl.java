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
import com.dasi.common.exception.SendException;
import com.dasi.common.properties.RabbitMqProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.channel.DlxSender;
import com.dasi.core.mapper.MessageMapper;
import com.dasi.core.service.*;
import com.dasi.pojo.dto.DispatchPageDTO;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.entity.Contact;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
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

}
