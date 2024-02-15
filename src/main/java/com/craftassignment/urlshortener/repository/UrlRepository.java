package com.craftassignment.urlshortener.repository;


import com.craftassignment.urlshortener.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<UrlEntity, Long> {

    @Query("SELECT u FROM url u WHERE u.originalUrl = ?1")
    List<UrlEntity> findUrlByOriginalUrl(String fullUrl);

    @Query("SELECT u FROM url u WHERE u.expiryPeriod <= ?1")
    List<UrlEntity> findByExpiryTimeBefore(LocalDateTime expiryPeriod);

    @Query("SELECT u FROM url u WHERE u.shortUrl = ?1")
    UrlEntity findByShortUrl(String shortUrl);
}
