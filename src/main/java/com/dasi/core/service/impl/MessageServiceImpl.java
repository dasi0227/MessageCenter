package com.dasi.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dasi.common.annotation.AutoFill;
import com.dasi.common.constant.DefaultConstant;
import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.FillType;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.common.properties.RabbitMqProperties;
import com.dasi.common.result.PageResult;
import com.dasi.core.mapper.ContactMapper;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.core.mapper.MessageMapper;
import com.dasi.core.service.ContactService;
import com.dasi.core.service.DispatchService;
import com.dasi.core.service.MessageService;
import com.dasi.pojo.dto.MessagePageDTO;
import com.dasi.pojo.dto.MessageSendDTO;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Message;
import com.dasi.pojo.vo.MessageDetailVO;
import com.dasi.pojo.vo.MessagePageVO;
import com.dasi.util.SensitiveWordDetectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private DispatchMapper dispatchMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    @Autowired
    private SensitiveWordDetectUtil sensitiveWordDetectUtil;

    @Autowired
    private DispatchService dispatchService;

    @Autowired
    private ContactService contactService;

    @Override
    @AutoFill(FillType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(MessageSendDTO dto) {
        // 创建消息
        Message message = BeanUtil.copyProperties(dto, Message.class);
        save(message);

        // 发到每个收件人
        Long sendFrom = AccountContextHolder.get().getId();
        List<Dispatch> dispatchList = new ArrayList<>();
        for (Long sendTo : dto.getContactIds()) {
            String target = contactService.resolveTarget(sendTo, dto.getChannel());
            Dispatch dispatch = Dispatch.builder()
                    .msgId(message.getId())
                    .channel(dto.getChannel())
                    .status(MsgStatus.PENDING)
                    .sendFrom(sendFrom)
                    .sendTo(sendTo)
                    .target(target)
                    .createdAt(LocalDateTime.now())
                    .build();
            dispatchMapper.insert(dispatch);
            dispatchList.add(dispatch);
        }

        // 敏感词处理
        String sensitiveWords = sensitiveWordDetectUtil.detect(message);
        if (StrUtil.isNotEmpty(sensitiveWords)) {
            String errorMsg = DefaultConstant.SENSITIVE_WORD_WARNING + sensitiveWords;
            for (Dispatch dispatch : dispatchList) {
                dispatchService.updateFailStatus(dispatch.getId(), errorMsg);
            }
            log.warn("【Message Service】发送消息失败，检测到敏感词：{}", sensitiveWords);
            return;
        }

        // 投递到 MQ
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (Dispatch dispatch : dispatchList) {
                    String route = dto.getChannel().getRoute(rabbitMqProperties);
                    rabbitTemplate.convertAndSend(
                            rabbitMqProperties.getExchange(),
                            route,
                            dispatch.getId()
                    );
                }
            }
        });

        log.debug("【Message Service】发送消息成功：{}", dto);
    }

    @Override
    public PageResult<MessagePageVO> getMessagePage(MessagePageDTO dto) {
        Page<MessagePageVO> param = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<MessagePageVO> result = messageMapper.selectMessagePage(param, dto);
        log.debug("【Message Service】分页查询消息：{}", dto);
        return PageResult.of(result);
    }

    @Override
    public MessageDetailVO getMessageDetail(Long dispatchId) {
        MessageDetailVO vo = messageMapper.selectMessageDetail(dispatchId);
        log.debug("【Message Service】查询消息详情：{}", vo);
        return vo;
    }
}
