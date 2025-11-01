package com.dasi;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dasi.core.mapper")
@Slf4j
public class MessageServer {
    public static void main(String[] args) {
        SpringApplication.run(MessageServer.class, args);
        log.debug("Message Center start successfully!!\n\n\n");
    }
}

