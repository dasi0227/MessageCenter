package com.dasi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

@Configuration
@Slf4j
public class SpaRedirectConfig {

    @Bean
    public RouterFunction<ServerResponse> spaRouter() {

        // 静态资源后缀（白名单，只排除这些）
        List<String> staticExtensions = List.of(
                ".js", ".css", ".png", ".jpg", ".jpeg", ".gif",
                ".svg", ".ico", ".webp", ".map", ".json"
        );

        RequestPredicate spaPredicate = request -> {
            String path = request.path();

            if (path.startsWith("/api") || path.startsWith("/ws") || "/favicon.ico".equals(path)) {
                return false;
            }

            for (String ext : staticExtensions) {
                if (path.endsWith(ext)) {
                    return false;
                }
            }

            return true;
        };

        log.info("【Config】Initialized Successfully：SpaRedirect");

        return RouterFunctions.route(
                spaPredicate,
                req -> ServerResponse.ok().body(new ClassPathResource("static/index.html"))
        );
    }
}