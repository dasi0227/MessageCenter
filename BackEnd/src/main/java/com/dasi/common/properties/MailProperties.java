package com.dasi.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "dasi.mail")
public class MailProperties {
    private Integer port;
    private List<AuthCode> authCodes;

    @Data
    public static class AuthCode {
        private String email;
        private String password;
        private String host;
    }
}
