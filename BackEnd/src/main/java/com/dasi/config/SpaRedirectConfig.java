package com.dasi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@SuppressWarnings("all")
@Configuration
@Slf4j
public class SpaRedirectConfig {

    @Bean
    public RouterFunction<ServerResponse> spaRouter() {
        RequestPredicate spaPredicate = request -> {
            String path = request.path();

            if (path.startsWith("/api") || path.startsWith("/ws")) {
                return false;
            }

            if (path.startsWith("/assets") ||
                    path.startsWith("/static") ||
                    path.startsWith("/webjars") ||
                    path.startsWith("/favicon.ico") ||
                    path.startsWith("/.well-known")) {
                return false;
            }

            return true;
        };

        log.info("【Config】Initialized Successfully：SpaRedirect");

        return RouterFunctions.route(
                spaPredicate,
                req -> ServerResponse.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(new ClassPathResource("static/index.html"))
        );
    }
}