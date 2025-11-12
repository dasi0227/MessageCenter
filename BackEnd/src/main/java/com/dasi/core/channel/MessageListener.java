package com.dasi.core.channel;

import com.dasi.web.websocket.WebSocketServer;
import com.dasi.core.service.DispatchService;
import com.dasi.core.service.FailureService;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Failure;
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

    @Autowired
    private FailureService failureService;

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

    @RabbitListener(queues = "#{@rabbitMqProperties.getDlxQueue()}")
    public void listenDlx(Failure failure) {
        log.debug("【Listener】监听到死信：{}", failure);
        failureService.save(failure);
        WebSocketServer.broadcast("出现严重的错误消息，请及时处理！");
    }
}