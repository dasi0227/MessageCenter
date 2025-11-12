package com.dasi.config;

import com.dasi.common.properties.MailSenderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Configuration
public class MailSenderConfig {
    @Autowired
    private MailSenderProperties mailSenderProperties;

    @Bean("departmentMailMap")
    public Map<String, JavaMailSender> mailSenderMap() {
        Map<String, JavaMailSender> map = new HashMap<>();

        mailSenderProperties.getAuthCodes().forEach((email, auth) -> {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(auth.getHost());
            sender.setPort(mailSenderProperties.getPort());
            sender.setUsername(email);
            sender.setPassword(auth.getPassword());
            sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

            Properties props = sender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            map.put(email, sender);
        });

        log.info("JavaMailSender Initialized Successfully");
        return map;
    }

}
