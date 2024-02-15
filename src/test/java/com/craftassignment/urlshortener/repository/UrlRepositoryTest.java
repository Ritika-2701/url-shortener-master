package com.craftassignment.urlshortener.repository;

import com.craftassignment.urlshortener.model.UrlEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class UrlRepositoryTest {

    @Mock
    private UrlRepository urlRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById() {
        Long id = 1L;
        UrlEntity urlEntity = new UrlEntity("http://example.com", "http://short.ly/123", null);
        when(urlRepository.findById(id)).thenReturn(Optional.of(urlEntity));

        Optional<UrlEntity> foundEntity = urlRepository.findById(id);

        assertTrue(foundEntity.isPresent());
        assertEquals(urlEntity, foundEntity.get());
    }

    @Test
    public void testFindByShortUrl() {
        String shortUrl = "http://short.ly/123";
        UrlEntity urlEntity = new UrlEntity("http://example.com", shortUrl, null);
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(urlEntity);

        UrlEntity foundEntity = urlRepository.findByShortUrl(shortUrl);

        assertNotNull(foundEntity);
        assertEquals(urlEntity, foundEntity);
    }

    @Test
    public void testSave() {
        UrlEntity urlEntity = new UrlEntity("http://example.com", "http://short.ly/123", null);
        when(urlRepository.save(any())).thenReturn(urlEntity);

        UrlEntity savedEntity = urlRepository.save(urlEntity);

        assertEquals(urlEntity, savedEntity);
    }

    @Test
    public void testSaveAll() {
        List<UrlEntity> urlEntities = new ArrayList<>();

        when(urlRepository.saveAll(urlEntities)).thenReturn(urlEntities);

        List<UrlEntity> savedEntities = urlRepository.saveAll(urlEntities);

        assertNotNull(savedEntities);
        assertEquals(urlEntities, savedEntities);
    }
}
