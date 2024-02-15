package com.craftassignment.urlshortener.service;

import com.craftassignment.urlshortener.cache.RedisCache;
import com.craftassignment.urlshortener.model.UrlEntity;
import com.craftassignment.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Configuration
@EnableScheduling
public class UrlExpirationService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private RedisCache redisCache;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void removeExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        List<UrlEntity> expiredUrls = urlRepository.findByExpiryTimeBefore(now);
        redisCache.remove(expiredUrls);
        urlRepository.deleteAll(expiredUrls);
    }
}

