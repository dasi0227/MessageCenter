package com.dasi.util;

import com.dasi.common.constant.RedisConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class InboxUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(RedisConstant.INBOX_KEY))) {
            redisTemplate.opsForValue().set(RedisConstant.INBOX_KEY, "0");
        }
    }

    public String nextId() {
        Long inboxId = redisTemplate.opsForValue().increment(RedisConstant.INBOX_KEY);
        return String.format("%06d", inboxId);
    }
}
