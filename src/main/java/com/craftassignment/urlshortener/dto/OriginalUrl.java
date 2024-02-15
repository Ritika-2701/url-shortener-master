package com.craftassignment.urlshortener.dto;


import lombok.Getter;


public class OriginalUrl {

    @Getter
    private String originalUrl;

    public OriginalUrl() {
    }

    public OriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    public String getOriginalUrl() {
        return this.originalUrl;
    }


}
