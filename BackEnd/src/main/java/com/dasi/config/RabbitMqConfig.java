package com.dasi.config;

import com.dasi.common.enumeration.MsgChannel;
import com.dasi.common.properties.RabbitMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class RabbitMqConfig {

    @Autowired
    private RabbitMqProperties rabbitMqProperties;

    @Bean
    public Declarables declareBindings() {
        List<Declarable> list = new ArrayList<>();

        // 主交换机
        DirectExchange exchange = ExchangeBuilder
                .directExchange(rabbitMqProperties.getExchange())
                .delayed()
                .durable(true)
                .build();
        list.add(exchange);

        // 各业务队列绑定
        for (MsgChannel channel : MsgChannel.values()) {
            Queue queue = QueueBuilder
                    .durable(channel.getQueue(rabbitMqProperties))
                    .withArgument("x-dead-letter-exchange", rabbitMqProperties.getDlxExchange())
                    .withArgument("x-dead-letter-routing-key", rabbitMqProperties.getDlxRoute())
                    .build();
            Binding binding = BindingBuilder
                    .bind(queue)
                    .to(exchange)
                    .with(channel.getRoute(rabbitMqProperties));

            list.add(queue);
            list.add(binding);
        }

        DirectExchange dlxExchange = ExchangeBuilder
                .directExchange(rabbitMqProperties.getDlxExchange())
                .durable(true)
                .build();
        list.add(dlxExchange);
        list.add(dlxExchange);

        Queue dlqQueue = QueueBuilder.durable(rabbitMqProperties.getDlxQueue()).build();
        Binding dlqBinding = BindingBuilder
                .bind(dlqQueue)
                .to(dlxExchange)
                .with(rabbitMqProperties.getDlxRoute());
        list.add(dlqQueue);
        list.add(dlqBinding);

        log.info("RabbitMQ Initialized Successfully");
        return new Declarables(list);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
