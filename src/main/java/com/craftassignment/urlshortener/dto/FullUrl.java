package com.craftassignment.urlshortener.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public class FullUrl {

    private String fullUrl;

    public FullUrl() {
    }

    public FullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }
    public String getFullUrl() {
        return fullUrl;
    }


}
