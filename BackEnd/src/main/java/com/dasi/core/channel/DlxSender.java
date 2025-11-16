package com.dasi.core.channel;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.dasi.common.enumeration.FailureStatus;
import com.dasi.common.properties.RabbitMqProperties;
import com.dasi.pojo.entity.Dispatch;
import com.dasi.pojo.entity.Failure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class DlxSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    public void sendDlx(Dispatch dispatch, Exception exception) {
        Failure failure = Failure.builder()
                .dispatchId(dispatch.getId())
                .errorType(exception.getClass().getName())
                .errorMessage(exception.getMessage())
                .errorStack(ExceptionUtil.stacktraceToString(exception))
                .status(FailureStatus.UNHANDLED)
                .createdAt(LocalDateTime.now())
                .payload(dispatch)
                .build();
        log.debug("【DlxSender】消息发送失败，转发到死信队列：{}", dispatch);
        rabbitTemplate.convertAndSend(rabbitMqProperties.getDlxExchange(), rabbitMqProperties.getDlxRoute(), failure);
    }

}
