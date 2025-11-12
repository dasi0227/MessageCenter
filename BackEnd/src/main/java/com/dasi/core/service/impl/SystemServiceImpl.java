package com.dasi.core.service.impl;

import com.dasi.common.constant.RedisConstant;
import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.MessageCenterException;
import com.dasi.core.service.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class SystemServiceImpl implements SystemService {

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void flush(String entity) {
        String cacheName = switch (entity.toLowerCase()) {
            case "account"      -> RedisConstant.CACHE_ACCOUNT_PREFIX;
            case "contact"      -> RedisConstant.CACHE_CONTACT_PREFIX;
            case "department"   -> RedisConstant.CACHE_DEPARTMENT_PREFIX;
            case "message"      -> RedisConstant.CACHE_MESSAGE_PREFIX;
            case "dispatch"     -> RedisConstant.CACHE_DISPATCH_PREFIX;
            case "template"     -> RedisConstant.CACHE_TEMPLATE_PREFIX;
            case "render"       -> RedisConstant.CACHE_RENDER_PREFIX;
            case "sensitive"    -> RedisConstant.CACHE_SENSITIVE_WORD_PREFIX;
            case "dashboard"    -> RedisConstant.CACHE_DASHBOARD_PREFIX;
            case "failure"      -> RedisConstant.CACHE_FAILURE_PREFIX;
            case "mailbox"      -> RedisConstant.CACHE_MAILBOX_PREFIX;
            default             -> null;
        };

        if (cacheName == null) {
            log.error("【System Service】未识别的实体名称：{}", entity);
            throw new MessageCenterException(ResultInfo.PATH_VALIDATE_FAIL);
        }

        Cache cache = redisCacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            log.info("【System Service】已清空缓存模块：{}", cacheName);
        } else {
            Set<String> keys = redisTemplate.keys(cacheName + "*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("【System Service】已手动清除 Redis Key：{}", cacheName);
            } else {
                log.warn("【System Service】未找到缓存模块或 Redis key：{}", cacheName);
            }
        }
    }
}
