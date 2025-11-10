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
    public DirectExchange exchangeMessageCenter() {
        return ExchangeBuilder
                .directExchange(rabbitMqProperties.getExchange())
                .delayed()
                .durable(true)
                .build();
    }

    @Bean
    public Declarables declareBindings() {
        DirectExchange exchange = exchangeMessageCenter();
        List<Declarable> list = new ArrayList<>();

        for (MsgChannel channel : MsgChannel.values()) {
            String queueName = channel.getQueue(rabbitMqProperties);
            String routeKey = channel.getRoute(rabbitMqProperties);

            Queue queue = new Queue(queueName, true);
            Binding binding = BindingBuilder.bind(queue).to(exchange).with(routeKey);

            list.add(queue);
            list.add(binding);
        }

        log.info("RabbitMQ Init Successfully");
        return new Declarables(list);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
