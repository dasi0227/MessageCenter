package com.dasi.web.interceptor;

import com.dasi.common.context.ContactContext;
import com.dasi.common.context.ContactContextHolder;
import com.dasi.common.properties.JwtProperties;
import com.dasi.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtContactInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截 Controller 请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取 token
        String token = request.getHeader(jwtProperties.getContactTokenName());

        // 校验令牌
        Claims claims = jwtUtil.parseToken(token);
        Long id = Long.valueOf(claims.get(jwtProperties.getContactIdClaimKey()).toString());
        Long inbox = Long.valueOf(claims.get(jwtProperties.getContactInboxClaimKey()).toString());
        ContactContextHolder.set(new ContactContext(id, inbox));

        log.debug("【令牌校验成功】当前联系人 ID：{}", id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ContactContextHolder.clear();
    }

}
