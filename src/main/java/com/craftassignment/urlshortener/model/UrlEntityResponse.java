package com.craftassignment.urlshortener.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UrlEntityResponse {

    @JsonProperty("originalUrl")
    private String originalUrl;

    @JsonProperty("customUrl")
    private String shortUrl;

    public UrlEntityResponse(String originalUrl, String shortUrl){
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
    }


}