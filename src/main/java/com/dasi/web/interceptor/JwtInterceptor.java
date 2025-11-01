package com.dasi.web.interceptor;

import com.dasi.common.enumeration.ResultInfo;
import com.dasi.common.exception.JwtErrorException;
import com.dasi.common.properties.JwtProperties;
import com.dasi.util.UserContextUtil;
import com.dasi.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

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

        log.debug("请求路径：{}", request.getRequestURI());

        // 获取 token
        String token = request.getHeader(jwtProperties.getTokenName());

        // 校验令牌
        Claims claims = jwtUtil.parseToken(token);
        Long userId = Long.valueOf(claims.get(jwtProperties.getClaimUserKey()).toString());
        UserContextUtil.setUser(userId);

        log.debug("JWT 校验成功，当前管理员 ID：{}", userId);
        return true;
    }
}
