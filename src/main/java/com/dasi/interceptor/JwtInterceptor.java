package com.dasi.interceptor;

import com.dasi.pojo.enumeration.ResultInfo;
import com.dasi.pojo.exception.JwtErrorException;
import com.dasi.pojo.properties.JwtProperties;
import com.dasi.util.AdminContextUtil;
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
        if (token == null || token.isBlank()) {
            throw new JwtErrorException(ResultInfo.TOKEN_MISSING);
        }

        // 校验令牌
        try {
            Claims claims = jwtUtil.parseToken(token);
            Long adminId = Long.valueOf(claims.get(jwtProperties.getAdminIdKey()).toString());
            AdminContextUtil.setAdmin(adminId);
            log.debug("JWT 校验成功，当前管理员 ID：{}", adminId);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT 校验失败：{}", e.getMessage());
            throw new JwtErrorException(ResultInfo.TOKEN_EXPIRED);
        } catch (JwtException exception) {
            log.error("JWT 校验失败：{}", exception.getMessage());
            throw new JwtErrorException(ResultInfo.TOKEN_ERROR);
        }
    }
}
