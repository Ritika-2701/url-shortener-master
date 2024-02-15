package com.craftassignment.urlshortener.Dao;

import com.craftassignment.urlshortener.cache.RedisCache;
import com.craftassignment.urlshortener.dao.UrlServiceDao;
import com.craftassignment.urlshortener.repository.UrlRepository;
import com.craftassignment.urlshortener.utils.ZookeeperCounterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


class UrlServiceDaoTest {

    @Mock
    private ZookeeperCounterManager counterManager;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisCache redisCache;

    @InjectMocks
    private UrlServiceDao urlServiceDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGenerateUniqueShortUrl() {
        // Arrange
        when(counterManager.getNextCounter()).thenReturn(1L);
        when(redisCache.smembers(anyString())).thenReturn(Set.of());

        // Act
        String shortUrl = urlServiceDao.generateUniqueShortUrl();

        // Assert
        assertEquals("http://short.url/1", shortUrl);
    }

    @Test
    void testValidateCustomShortURL_NotExists() {
        // Arrange
        when(redisCache.smembers(anyString())).thenReturn(Set.of());

        // Act
        String validatedUrl = urlServiceDao.validateCustomShortURL("custom");

        // Assert
        assertEquals("custom", validatedUrl);
    }

    @Test
    void testValidateCustomShortURL_Exists() {
        // Arrange
        when(redisCache.smembers(anyString())).thenReturn(Set.of("http://short.url/custom"));

        // Act
        String validatedUrl = urlServiceDao.validateCustomShortURL("custom");

        // Assert
        assertNull(validatedUrl);
    }

    // Additional test cases can be added for other methods...
}
