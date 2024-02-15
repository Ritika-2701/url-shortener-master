package com.craftassignment.urlshortener.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.craftassignment.urlshortener.dto.OriginalUrl;
import com.craftassignment.urlshortener.dto.ShortUrl;
import com.craftassignment.urlshortener.dto.URLRequestDTO;
import com.craftassignment.urlshortener.model.UrlEntityResponse;
import com.craftassignment.urlshortener.service.UrlService;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveUrl() throws Exception {
        URLRequestDTO requestDTO = new URLRequestDTO();
        ShortUrl shortUrl = new ShortUrl("http://short.ly/123");
        requestDTO.originalUrl = new OriginalUrl("http://example.com");

        when(urlService.getShortURL(requestDTO)).thenReturn(shortUrl);
        ResponseEntity<Object> response = urlController.saveUrl(requestDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testSaveUrlInBulk() throws Exception {
        // Given
        List<URLRequestDTO> requestDTOs = new ArrayList<>();
        List<UrlEntityResponse> urlEntityResponses = new ArrayList<>();

        // When
        when(urlService.getShortURLs(requestDTOs)).thenReturn(urlEntityResponses);

        // Then
        ResponseEntity<Object> response = urlController.saveUrlInBulk(requestDTOs);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(urlEntityResponses, response.getBody());
    }

    @Test
    public void testRedirectToFullUrl() throws Exception {
        String shortUrl = "http://short.ly/123";
        OriginalUrl originalUrl = new OriginalUrl("http://example.com");
        HttpServletResponse responseMock = mock(HttpServletResponse.class);

        when(urlService.getOriginalUrl(anyString())).thenReturn(originalUrl);
        urlController.redirectToFullUrl(responseMock, shortUrl);
        verify(responseMock, times(1)).sendRedirect(originalUrl.getOriginalUrl());
    }
    @Test
    public void testRedirectToFullUrlThrowsException() throws Exception {
        String shortUrl = "http://short.ly/123";
        OriginalUrl originalUrl = new OriginalUrl("http://example.com");
        HttpServletResponse responseMock = mock(HttpServletResponse.class);

        NotFoundException notFoundException = new NotFoundException("URL not found");
        when(urlService.getOriginalUrl(anyString())).thenThrow(notFoundException);
        assertThrows(ResponseStatusException.class, () -> urlController.redirectToFullUrl(responseMock, shortUrl));
    }
}
