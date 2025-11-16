package com.dasi.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "dasi.mail")
public class MailProperties {
    private Integer port;
    private Map<String, AuthCode> authCodes;

    @Data
    public static class AuthCode {
        private String password;
        private String host;
    }
}
