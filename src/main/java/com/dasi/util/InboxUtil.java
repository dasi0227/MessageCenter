package com.dasi.util;

import com.dasi.common.constant.RedisConstant;
import com.dasi.core.mapper.ContactMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InboxUtil {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ContactMapper contactMapper;

    @PostConstruct
    public void init() {
        Long maxInbox = contactMapper.findMaxInbox();
        if (maxInbox == null) {
            maxInbox = 0L;
        }
        redisTemplate.opsForValue().set(RedisConstant.INBOX_KEY, String.valueOf(maxInbox));
    }

    public Long nextId() {
        return redisTemplate.opsForValue().increment(RedisConstant.INBOX_KEY);
    }

    public String format(Long inbox) {
        return String.format("%06d", inbox);
    }
}
