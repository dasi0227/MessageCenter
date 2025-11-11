package com.dasi.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "dasi.rabbitmq")
@Data
public class RabbitMqProperties {
    private String exchange;
    private Map<String, String> route;
    private Map<String, String> queue;
    private String dlxExchange;
    private String dlxQueue;
    private String dlxRoute;
}