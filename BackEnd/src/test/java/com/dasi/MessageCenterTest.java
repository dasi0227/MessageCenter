package com.dasi;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
@Slf4j
public class MessageCenterTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testMySQL() {
        try (Connection connection = dataSource.getConnection()) {
            log.debug("MySQL connected: {}", connection.getMetaData().getURL());
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void testRedis() {
        redisTemplate.opsForValue().set("test:key", "hello");
        String value = redisTemplate.opsForValue().get("test:key");
        log.debug("Get redis test:key: {}", value);
    }

    @Test
    public void testRabbitProducer() {
        String message = "Hello MQ";
        rabbitTemplate.convertAndSend("test-exchange", "test-key", message);
        log.debug("Send message: {}", message);
    }
}
