package com.craftassignment.urlshortener.dao;

import com.craftassignment.urlshortener.cache.RedisCache;
import com.craftassignment.urlshortener.dto.OriginalUrl;
import com.craftassignment.urlshortener.dto.ShortUrl;
import com.craftassignment.urlshortener.error.InvalidUrlException;
import com.craftassignment.urlshortener.model.UrlEntity;
import com.craftassignment.urlshortener.service.UrlService;
import com.craftassignment.urlshortener.utils.ShorteningUtil;
import com.craftassignment.urlshortener.utils.ZookeeperCounterManager;
import com.craftassignment.urlshortener.repository.UrlRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.craftassignment.urlshortener.utils.ApplicationUtils.*;

@Component
public class UrlServiceDao {
    private final Logger logger = LoggerFactory.getLogger(UrlService.class);
    @Autowired
    private final UrlRepository urlRepository;
    @Autowired
    private final ZookeeperCounterManager counterManager;
    @Autowired
    private final RedisCache redisCache;


    public UrlServiceDao(ZookeeperCounterManager counterManager, UrlRepository urlRepository, UrlRepository urlRepository1, RedisCache redisCache) {
        this.counterManager = counterManager;
        this.urlRepository = urlRepository1;
        this.redisCache = redisCache;
    }
    public String generateUniqueShortUrl() {
        String base62Encoded;
        long counter;
        do {
            counter = counterManager.getNextCounter();
            base62Encoded = ShorteningUtil.idToStr(counter);
        } while (isShortUrlExists(base62Encoded));

        return String.format("%s%s", BASE_SHORT_URL, base62Encoded);
    }
    private boolean isShortUrlExists(String shortUrl) {
        Set<String> shortUrls = redisCache.smembers(SHORT_URL_SET);
        return shortUrls.contains(BASE_SHORT_URL+shortUrl);
    }

    public String validateCustomShortURL(String customShortUrl) {
        String validUrl = customShortUrl;
        if(isShortUrlExists(customShortUrl)) validUrl = null;
        return validUrl;
    }

    public ShortUrl getExistingShortURL(OriginalUrl originalUrl){
        if(Objects.isNull(originalUrl) || originalUrl.getOriginalUrl().isEmpty()){
            logger.error("full url is empty");
            throw new InvalidRequestStateException("Failed to generate short URL !");
        }
        List<UrlEntity> urlEntity = urlRepository.findUrlByOriginalUrl(originalUrl.getOriginalUrl());
        if(!urlEntity.isEmpty()){
            logger.info("short url is already exists for: "+ originalUrl.getOriginalUrl());
            return new ShortUrl(urlEntity.get(0).getShortUrl());
        }
        return null;
    }

    public ShortUrl getFinalShortURL(OriginalUrl originalUrl, String customShortUrl) throws InvalidUrlException {
        String shortUrlText;
        if(Objects.isNull(customShortUrl) || customShortUrl.isEmpty()) {
            shortUrlText = generateUniqueShortUrl();
        }
        else{
            shortUrlText = validateCustomShortURL(customShortUrl);
            if(Objects.isNull(shortUrlText))
                throw new InvalidUrlException("Short url is already exists for: "+ originalUrl.getOriginalUrl());
        }
        return new ShortUrl(shortUrlText);
    }

}
