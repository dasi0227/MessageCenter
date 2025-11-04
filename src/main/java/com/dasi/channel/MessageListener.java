package com.dasi.channel;

import com.dasi.core.service.DispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MessageListener {

    @Autowired
    private MailboxSender mailboxSender;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private SmsSender smsSender;

    @Autowired
    private DispatchService dispatchService;

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).MAILBOX.getQueue(@rabbitMqProperties)}")
    public void listenMailbox(Long dispatchId) {
        log.info("【监听器】发送站内信：{}", dispatchId);
        dispatchService.updateSendStatus(dispatchId);
        mailboxSender.send(dispatchId);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).SMS.getQueue(@rabbitMqProperties)}")
    public void listenSms(Long dispatchId) {
        log.info("【监听器】发送短信：{}", dispatchId);
        dispatchService.updateSendStatus(dispatchId);
        smsSender.send(dispatchId);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).EMAIL.getQueue(@rabbitMqProperties)}")
    public void listenEmail(Long dispatchId) {
        log.info("【监听器】发送邮件：{}", dispatchId);
        dispatchService.updateSendStatus(dispatchId);
        emailSender.send(dispatchId);
    }
}
