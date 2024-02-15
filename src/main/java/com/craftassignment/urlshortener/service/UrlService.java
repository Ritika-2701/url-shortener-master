package com.craftassignment.urlshortener.service;


import com.craftassignment.urlshortener.cache.RedisCache;
import com.craftassignment.urlshortener.dto.URLRequestDTO;
import com.craftassignment.urlshortener.model.UrlEntityResponse;
import com.craftassignment.urlshortener.dao.UrlServiceDao;
import com.craftassignment.urlshortener.dto.OriginalUrl;
import com.craftassignment.urlshortener.dto.ShortUrl;
import com.craftassignment.urlshortener.model.UrlEntity;
import com.craftassignment.urlshortener.repository.UrlRepository;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class UrlService {

    private final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private final UrlRepository urlRepository;
    @Autowired
    private final UrlServiceDao urlServiceDao;
   @Autowired
    private final RedisCache redisCache;



    @Autowired
    public UrlService(UrlRepository urlRepository, UrlServiceDao urlServiceDao, RedisCache redisCache) {
        this.urlRepository = urlRepository;
        this.urlServiceDao = urlServiceDao;
        this.redisCache = redisCache;
    }

    public UrlEntity get(Long id) {
        logger.info(String.format("Fetching Url from database for Id %d", id));
        Optional<UrlEntity> urlEntity = urlRepository.findById(id);
        if(urlEntity.isPresent()) return urlEntity.get();
        else {
            throw new NoSuchElementException("URL Entity is not found for id "+id);
        }
    }


    public OriginalUrl getOriginalUrl(String shortenString) throws NotFoundException {
        UrlEntity urlEntity = urlRepository.findByShortUrl(shortenString);
        if(Objects.isNull(urlEntity))
            throw new NotFoundException("Original URL is not found for: " + shortenString);
        return new OriginalUrl(urlEntity.getOriginalUrl());
    }

    public UrlEntity saveUrl(OriginalUrl originalUrl, String shortUrl, LocalDateTime expiryPeriod) {
        return urlRepository.save(new UrlEntity(originalUrl.getOriginalUrl(), shortUrl, expiryPeriod));
    }
    public List<UrlEntity> saveUrls(List<UrlEntity> urlEntities) {
        return urlRepository.saveAll(urlEntities);
    }
    public static List<UrlEntityResponse> toUrlEntityResponse(List<UrlEntity> urlEntities){
        List<UrlEntityResponse> urlEntityResponses = new ArrayList<>();
        for(UrlEntity urlEntity: urlEntities){
            urlEntityResponses.add(new UrlEntityResponse(urlEntity.getOriginalUrl(), urlEntity.getShortUrl()));
        }
        return urlEntityResponses;
    }


    public ShortUrl getShortURL(URLRequestDTO urlRequestDTO) throws Exception {
            ShortUrl existingShortURL = urlServiceDao.getExistingShortURL(urlRequestDTO.originalUrl);
            if(Objects.nonNull(existingShortURL) ) {
                return existingShortURL;
            }
            ShortUrl shortUrl = urlServiceDao.getFinalShortURL(urlRequestDTO.originalUrl, urlRequestDTO.customUrl);
            saveUrl(urlRequestDTO.originalUrl, shortUrl.getShortUrl(), urlRequestDTO.expiryPeriod);
            redisCache.set(urlRequestDTO.originalUrl,shortUrl);
            return shortUrl;
    }

    public List<UrlEntityResponse> getShortURLs(List<URLRequestDTO> urlRequestDTOs) throws Exception {
        List<UrlEntity> newUrlEntities = new ArrayList<>();
        List<UrlEntity> existingUrlEntities = new ArrayList<>();
        for (URLRequestDTO urlRequestDTO : urlRequestDTOs) {
            ShortUrl existingShortURL = urlServiceDao.getExistingShortURL(urlRequestDTO.originalUrl);
            if(Objects.nonNull(existingShortURL) ) {
                existingUrlEntities.add(new UrlEntity(urlRequestDTO.originalUrl.getOriginalUrl(), existingShortURL.getShortUrl(), urlRequestDTO.expiryPeriod));
            }
            else{
                ShortUrl shortUrl = urlServiceDao.getFinalShortURL(urlRequestDTO.originalUrl, urlRequestDTO.customUrl);
                newUrlEntities.add(new UrlEntity(urlRequestDTO.originalUrl.getOriginalUrl(), shortUrl.getShortUrl(), urlRequestDTO.expiryPeriod));

            }
        }
        saveUrls(newUrlEntities);
        redisCache.set(newUrlEntities);
        newUrlEntities.addAll(existingUrlEntities);
        return toUrlEntityResponse(newUrlEntities);
    }
}
