package com.dasi.core.channel;

import com.dasi.core.service.FailureService;
import com.dasi.pojo.entity.Failure;
import com.dasi.web.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DlxListener {

    @Autowired
    private FailureService failureService;

    @RabbitListener(queues = "#{@rabbitMqProperties.getDlxQueue()}")
    public void listenDlx(Failure failure) {
        log.debug("【Listener】监听到死信：{}", failure);
        failureService.save(failure);
        WebSocketServer.broadcast("发送消息出现严重的错误，请及时处理！");
    }

}
