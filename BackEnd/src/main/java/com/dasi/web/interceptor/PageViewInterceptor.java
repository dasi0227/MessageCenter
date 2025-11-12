package com.dasi.web.interceptor;

import cn.hutool.core.lang.UUID;
import com.dasi.common.constant.RedisConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class PageViewInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI().substring(4);

        log.debug("=========== 请求路径：{} ===========", path);

        String value = UUID.randomUUID().toString(true) + System.currentTimeMillis();

        String keyPath = RedisConstant.PV_KEY_PATH + path;
        redisTemplate.opsForHyperLogLog().add(keyPath, value);
        String keyTime = RedisConstant.PV_KEY_TIME + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        redisTemplate.opsForHyperLogLog().add(keyTime, value);

        return true;
    }
}
