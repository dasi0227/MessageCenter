package com.dasi.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Slf4j
public class TransactionConfig {

    @PostConstruct
    public void init() {
        log.info("【Config】Initialized Successfully：Transaction");
    }
}
