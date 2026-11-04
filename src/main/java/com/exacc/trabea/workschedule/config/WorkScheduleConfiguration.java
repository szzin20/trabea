package com.exacc.trabea.workschedule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class WorkScheduleConfiguration {
    @Bean
    Clock workScheduleClock() {
        return Clock.system(ZoneId.of("Asia/Jakarta"));
    }
}
