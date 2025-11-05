package com.dasi.web.interceptor;

import com.dasi.common.context.AccountContextHolder;
import com.dasi.common.enumeration.AccountRole;
import com.dasi.common.properties.JwtProperties;
import com.dasi.common.context.AccountContext;
import com.dasi.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class JwtAccountInterceptor implements HandlerInterceptor {

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
        String token = request.getHeader(jwtProperties.getAccountTokenName());

        // 校验令牌
        Claims claims = jwtUtil.parseToken(token);
        Long id = Long.valueOf(claims.get(jwtProperties.getAccountIdClaimKey()).toString());
        AccountRole role = AccountRole.valueOf(claims.get(jwtProperties.getAccountRoleClaimKey()).toString());
        AccountContextHolder.set(new AccountContext(id, role));

        log.debug("【令牌校验成功】当前管理员 ID：{}", id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        AccountContextHolder.clear();
    }
}
