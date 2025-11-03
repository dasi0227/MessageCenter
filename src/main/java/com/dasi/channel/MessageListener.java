package com.dasi.channel;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

    @RabbitListener(queues = "#{rabbitMqProperties.getQueue().get('inbox')}")
    public void listenInbox(Long dispatchId) {
        log.info("【监听器】发送 Inbox 消息：{}", dispatchId);
        changeDispatch(dispatchId);
        inboxSender.send(dispatchId);
    }

    @RabbitListener(queues = "#{rabbitMqProperties.getQueue().get('sms')}")
    public void listenSms(Long dispatchId) {
        log.info("【监听器】发送 SMS 消息：{}", dispatchId);
        changeDispatch(dispatchId);
        smsSender.send(dispatchId);
    }

    @RabbitListener(queues = "#{rabbitMqProperties.getQueue().get('email')}")
    public void listenEmail(Long dispatchId) {
        log.info("【监听器】发送 Email 消息：{}", dispatchId);
        changeDispatch(dispatchId);
        emailSender.send(dispatchId);
    }

    public void changeDispatch(Long dispatchId) {
        UpdateWrapper<Dispatch> wrapper = new UpdateWrapper<Dispatch>()
                .eq("id", dispatchId)
                .set("status", MsgStatus.SENDING)
                .set("sent_at", LocalDateTime.now());
        dispatchMapper.update(wrapper);
    }
}
