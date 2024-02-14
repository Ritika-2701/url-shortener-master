package com.craftassignment.urlshortener.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class ApplicationConfig {

    @Bean
    public AtomicLong currentCounter() {
        // Initialize the AtomicLong with the initial value
        return new AtomicLong(0);
    }
}

