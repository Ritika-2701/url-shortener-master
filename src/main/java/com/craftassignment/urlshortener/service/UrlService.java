package com.craftassignment.urlshortener.service;


import com.craftassignment.urlshortener.cache.RedisCache;
import com.craftassignment.urlshortener.dto.URLRequestDTO;
import com.craftassignment.urlshortener.error.InvalidUrlException;
import com.craftassignment.urlshortener.model.UrlEntityResponse;
import com.craftassignment.urlshortener.utils.ShorteningUtil;
import com.craftassignment.urlshortener.dao.UrlServiceDao;
import com.craftassignment.urlshortener.dto.FullUrl;
import com.craftassignment.urlshortener.dto.ShortUrl;
import com.craftassignment.urlshortener.model.UrlEntity;
import com.craftassignment.urlshortener.repository.UrlRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.craftassignment.urlshortener.model.UrlEntityResponse.toUrlEntityResponse;

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

    private UrlEntity get(Long id) {
        logger.info(String.format("Fetching Url from database for Id %d", id));
        Optional<UrlEntity> urlEntity = urlRepository.findById(id);
        if(urlEntity.isPresent()) return urlEntity.get();
        else {
            throw new NoSuchElementException("URL Entity is not found for id "+id);
        }
    }


    public FullUrl getFullUrl(String shortenString) {
        logger.debug("Converting Base 62 string %s to Base 10 id");
        Long id = ShorteningUtil.strToId(shortenString);
        logger.info(String.format("Converted Base 62 string %s to Base 10 id %s", shortenString, id));

        logger.info(String.format("Retrieving full url for %d", id));
        return new FullUrl(this.get(id).getFullUrl());
    }

    private UrlEntity saveUrl(FullUrl fullUrl, String shortUrl) {
        return urlRepository.save(new UrlEntity(fullUrl.getFullUrl(), shortUrl));
    }
    private List<UrlEntity> saveUrls(List<UrlEntity> urlEntities) {
        return urlRepository.saveAll(urlEntities);
    }


    public ShortUrl getShortURL(FullUrl fullUrl, String customShortUrl) throws Exception {
            ShortUrl shortUrl = urlServiceDao.getFinalShortURL(fullUrl, customShortUrl);
            saveUrl(fullUrl, shortUrl.getShortUrl());
            redisCache.set(fullUrl,shortUrl);
            return shortUrl;
    }

    public List<UrlEntityResponse> getShortURLs(List<URLRequestDTO> urlRequestDTOs) throws Exception {
        List<UrlEntity> urlEntities = new ArrayList<>();
        for (URLRequestDTO urlRequestDTO : urlRequestDTOs) {
            ShortUrl shortUrl = this.getShortURL(urlRequestDTO.fullUrl, urlRequestDTO.customUrl);
            urlEntities.add(new UrlEntity(urlRequestDTO.fullUrl.getFullUrl(), shortUrl.getShortUrl()));
        }
        saveUrls(urlEntities);
        redisCache.set(urlEntities);
        return toUrlEntityResponse(urlEntities);
    }
}
