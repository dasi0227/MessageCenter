package com.dasi.config;

import com.dasi.web.interceptor.JwtAccountInterceptor;
import com.dasi.web.interceptor.JwtContactInterceptor;
import com.dasi.web.interceptor.PageViewInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtAccountInterceptor jwtAccountInterceptor;

    @Autowired
    private PageViewInterceptor pageViewInterceptor;

    @Autowired
    private JwtContactInterceptor jwtContactInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pageViewInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/account/login",
                        "/api/account/register",
                        "/api/account/refresh",
                        "/ws/**"
                );

        registry.addInterceptor(jwtAccountInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/account/login",
                        "/api/account/register",
                        "/api/contact/login",
                        "/api/contact/mailbox",
                        "/api/mailbox/**",
                        "/ws/**"
                );

        registry.addInterceptor(jwtContactInterceptor)
                .addPathPatterns(
                        "/api/mailbox/**",
                        "/api/contact/mailbox"
                )
                .excludePathPatterns("/ws/**");

        log.info("Interceptors Initialized Successfully");
    }
}