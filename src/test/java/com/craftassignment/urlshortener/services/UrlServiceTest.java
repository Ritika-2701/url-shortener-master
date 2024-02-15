//package com.craftassignment.urlshortener.services;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.Mockito.when;
//
//import com.craftassignment.urlshortener.cache.RedisCache;
//import com.craftassignment.urlshortener.dao.UrlServiceDao;
//import com.craftassignment.urlshortener.dto.OriginalUrl;
//import com.craftassignment.urlshortener.dto.ShortUrl;
//import com.craftassignment.urlshortener.dto.URLRequestDTO;
//import com.craftassignment.urlshortener.error.InvalidUrlException;
//import com.craftassignment.urlshortener.model.UrlEntity;
//import com.craftassignment.urlshortener.model.UrlEntityResponse;
//import com.craftassignment.urlshortener.repository.UrlRepository;
//import com.craftassignment.urlshortener.service.UrlService;
//import com.craftassignment.urlshortener.utils.ZookeeperCounterManager;
//import javassist.NotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//public class UrlServiceTest {
//
//    @Mock
//    private UrlRepository urlRepository;
//
//    @Mock
//    private UrlServiceDao urlServiceDao;
//
//    @Mock
//    private RedisCache redisCache;
//
//    @Mock
//    private ZookeeperCounterManager zookeeperCounterManager;
//
//    @InjectMocks
//    private UrlService urlService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testGetOriginalUrl() throws NotFoundException {
//        String shortUrl = "http://short.ly/123";
//        UrlEntity urlEntity = new UrlEntity("http://example.com", shortUrl, null);
//        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(urlEntity));
//
//        OriginalUrl originalUrl = urlService.getOriginalUrl(shortUrl);
//        assertNotNull(originalUrl);
//        assertEquals(urlEntity.getOriginalUrl(), originalUrl.getOriginalUrl());
//    }
//
//    @Test
//    public void testGetShortURL() throws Exception {
//        URLRequestDTO requestDTO = new URLRequestDTO();
//        requestDTO.originalUrl = new OriginalUrl("http://example.com");
//        ShortUrl shortUrl = new ShortUrl("http://short.ly/123");
//        when(urlServiceDao.getFinalShortURL(any(), any())).thenReturn(shortUrl);
//        when(urlServiceDao.getExistingShortURL(any())).thenReturn(shortUrl);
//
//        ShortUrl generatedShortUrl = urlService.getShortURL(requestDTO);
//        assertNotNull(generatedShortUrl);
//        assertEquals(shortUrl, generatedShortUrl);
//    }
//
//    @Test
//    public void testGetShortURLs() throws Exception {
//        List<URLRequestDTO> requestDTOs = new ArrayList<>();
//        List<UrlEntityResponse> urlEntityResponses = new ArrayList<>();
//        when(urlServiceDao.getFinalShortURL(any(), any())).thenReturn(new ShortUrl("http://short.ly/123"));
//        when(urlRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
//
//        List<UrlEntityResponse> generatedResponses = urlService.getShortURLs(requestDTOs);
//
//        assertNotNull(generatedResponses);
//        assertEquals(urlEntityResponses, generatedResponses);
//    }
//
//    @Test
//    public void testGetExistingCustomShortURL() {
//        // Given
//        String originalUrl = "http://example.com";
//        String existingShortUrl = "http://short.ly/existing";
//        OriginalUrl originalUrlObject = new OriginalUrl(originalUrl);
//        when(urlRepository.findUrlByOriginalUrl(originalUrl)).thenReturn(List.of(new UrlEntity(originalUrl, existingShortUrl, null)));
//
//
//        ShortUrl customShortUrl = urlServiceDao.getExistingShortURL(originalUrlObject);
//        assertNotNull(customShortUrl);
//        assertEquals(existingShortUrl, customShortUrl.getShortUrl());
//    }
//
//    @Test
//    public void testGetFinalGenericShortURL() throws InvalidUrlException {
//        String originalUrl = "http://example.com";
//        OriginalUrl originalUrlObject = new OriginalUrl(originalUrl);
//        when(zookeeperCounterManager.getNextCounter()).thenReturn(1L);
//        when(redisCache.smembers(any())).thenReturn(new HashSet<>());
//
//        String finalShortUrl = urlServiceDao.generateUniqueShortUrl();
//        assertNotNull(finalShortUrl);
//        assertEquals("http://short.ly/a", finalShortUrl);
//    }
//}