package com.dasi.core.channel;

import com.dasi.common.enumeration.MsgStatus;
import com.dasi.core.service.DispatchService;
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

    @Autowired
    private DispatchService dispatchService;

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).MAILBOX.getQueue(@rabbitMqProperties)}")
    public void listenMailbox(Dispatch dispatch) {
        dispatchService.updateStatus(dispatch, MsgStatus.PROCESSING, null);
        log.debug("【Listener】监听到站内信：{}", dispatch);
        messageSender.sendMailbox(dispatch);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).EMAIL.getQueue(@rabbitMqProperties)}")
    public void listenEmail(Dispatch dispatch) {
        dispatchService.updateStatus(dispatch, MsgStatus.PROCESSING, null);
        log.debug("【Listener】监听到邮件：{}", dispatch);
        messageSender.sendEmail(dispatch);
    }

    @RabbitListener(queues = "#{T(com.dasi.common.enumeration.MsgChannel).WECOM.getQueue(@rabbitMqProperties)}")
    public void listenWeCom(Dispatch dispatch) {
        dispatchService.updateStatus(dispatch, MsgStatus.PROCESSING, null);
        log.debug("【Listener】监听到短信：{}", dispatch);
        messageSender.sendWeCom(dispatch);
    }

}