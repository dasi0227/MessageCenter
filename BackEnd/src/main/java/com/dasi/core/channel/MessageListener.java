package com.dasi.core.channel;

import com.dasi.pojo.entity.Dispatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class MessageListener {

    @Autowired
    private MessageSender messageSender;

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).MAILBOX.getQueue(@rabbitMqProperties)}")
    public void listenMailbox(Dispatch dispatch) {
        log.debug("【Listener】监听到站内信：{}", dispatch);
        messageSender.sendMailbox(dispatch);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).EMAIL.getQueue(@rabbitMqProperties)}")
    public void listenEmail(Dispatch dispatch) {
        log.debug("【Listener】监听到邮件：{}", dispatch);
        messageSender.sendEmail(dispatch);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).SMS.getQueue(@rabbitMqProperties)}")
    public void listenSms(Dispatch dispatch) {
        log.debug("【Listener】监听到短信：{}", dispatch);
        messageSender.sendSms(dispatch);
    }

}