package com.craftassignment.urlshortener.cache;

import com.craftassignment.urlshortener.dto.FullUrl;
import com.craftassignment.urlshortener.dto.ShortUrl;
import com.craftassignment.urlshortener.model.UrlEntity;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

import static com.craftassignment.urlshortener.utils.ApplicationUtils.FULL_URL_SET;
import static com.craftassignment.urlshortener.utils.ApplicationUtils.SHORT_URL_SET;

@Component
public class RedisCache {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 6379;

    private Jedis jedis;

    public RedisCache() {
        this.jedis = new Jedis(REDIS_HOST, REDIS_PORT);
    }

    public void set(FullUrl fullUrl, ShortUrl shortUrl){
        jedis.sadd(FULL_URL_SET, fullUrl.getFullUrl());
        jedis.sadd(SHORT_URL_SET, shortUrl.getShortUrl());
    }
    public void set(String fullUrl, String shortUrl){
        jedis.sadd(FULL_URL_SET, fullUrl);
        jedis.sadd(SHORT_URL_SET, shortUrl);
    }
    public void set(List<UrlEntity> urlEntities){
        for(UrlEntity urlEntity: urlEntities){
            set(urlEntity.getFullUrl(),urlEntity.getShortUrl());
        }
    }

    public void sadd(String key, String... members) {
        jedis.sadd(key, members);
    }

    public Set<String> smembers(String key) {
        return jedis.smembers(key);
    }

    public boolean exists(String key) {
        return jedis.exists(key);
    }

    public void close() {
        jedis.close();
    }
    public void removeFromSet(String key, String... members) {
        jedis.srem(key, members);
    }

}
