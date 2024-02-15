package com.craftassignment.urlshortener.controller;

import com.craftassignment.urlshortener.dto.OriginalUrl;
import com.craftassignment.urlshortener.dto.ShortUrl;
import com.craftassignment.urlshortener.dto.URLRequestDTO;
import com.craftassignment.urlshortener.model.UrlEntityResponse;
import com.craftassignment.urlshortener.service.UrlService;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class UrlController {

    Logger logger = LoggerFactory.getLogger(UrlController.class);

    protected final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Object> saveUrl(@RequestBody URLRequestDTO urlRequestDTO) throws Exception {
        ShortUrl shortUrl = urlService.getShortURL(urlRequestDTO);
        return ResponseEntity.ok(shortUrl);
    }
    @PostMapping("/bulk-shorten")
    public ResponseEntity<Object> saveUrlInBulk(@RequestBody List<URLRequestDTO> urlRequestDTOs) throws Exception {
        List<UrlEntityResponse> urlEntityResponses = urlService.getShortURLs(urlRequestDTOs);
        return ResponseEntity.ok(urlEntityResponses);
    }


    @GetMapping("/")
    public void redirectToFullUrl(HttpServletResponse response, @RequestParam String shortUrl) {
        try {
            OriginalUrl originalUrl = urlService.getOriginalUrl(shortUrl);
            logger.info(String.format("Redirecting to %s", originalUrl.getOriginalUrl()));

            // Redirects the response to the full url
            response.sendRedirect(originalUrl.getOriginalUrl());
        } catch (NotFoundException e) {
            logger.error(String.format("No URL found for %s in the db", shortUrl));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Url not found", e);
        } catch (IOException e) {
            logger.error("Could not redirect to the full url");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not redirect to the full url", e);
        }
    }

}
