package com.craftassignment.urlshortener.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class URLRequestDTO {
    public OriginalUrl originalUrl;
    @Nullable
    public String customUrl;
    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime expiryPeriod;

}
