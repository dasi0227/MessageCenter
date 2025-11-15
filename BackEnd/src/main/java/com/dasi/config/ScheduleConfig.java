package com.dasi.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {

    @PostConstruct
    public void init() {
        log.info("【Config】Initialized Successfully：Schedule");
    }

}
