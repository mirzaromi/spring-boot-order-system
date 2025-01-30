package org.mirza.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class CommonConfig {

    @Bean
    public Random random() {
        return new Random();
    }
}
