package com.dasi.channel;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.mapper.DispatchMapper;
import com.dasi.pojo.entity.Dispatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MessageListener {

    @Autowired
    private InboxSender inboxSender;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private DispatchMapper dispatchMapper;

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).INBOX.getQueue(@rabbitMqProperties)}")
    public void listenInbox(Long dispatchId) {
        log.info("【监听器】发送 Inbox 消息：{}", dispatchId);
        changeDispatchStatus(dispatchId);
        inboxSender.send(dispatchId);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).SMS.getQueue(@rabbitMqProperties)}")
    public void listenSms(Long dispatchId) {
        log.info("【监听器】发送 SMS 消息：{}", dispatchId);
        changeDispatchStatus(dispatchId);
        smsSender.send(dispatchId);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).EMAIL.getQueue(@rabbitMqProperties)}")
    public void listenEmail(Long dispatchId) {
        log.info("【监听器】发送 Email 消息：{}", dispatchId);
        changeDispatchStatus(dispatchId);
        emailSender.send(dispatchId);
    }

    public void changeDispatchStatus(Long dispatchId) {
        dispatchMapper.update(new LambdaUpdateWrapper<Dispatch>()
                .eq(Dispatch::getId, dispatchId)
                .set(Dispatch::getStatus, MsgStatus.SENDING)
                .set(Dispatch::getSentAt, LocalDateTime.now())
        );
    }
}
