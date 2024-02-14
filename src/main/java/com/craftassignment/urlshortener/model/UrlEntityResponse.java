package com.craftassignment.urlshortener.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class UrlEntityResponse {

    private String fullUrl;

    private String shortUrl;

    UrlEntityResponse(String fullUrl, String shortUrl){
        this.fullUrl = fullUrl;
        this.shortUrl = shortUrl;
    }

    public static List<UrlEntityResponse> toUrlEntityResponse(List<UrlEntity> urlEntities){
        List<UrlEntityResponse> urlEntityResponses = new ArrayList<>();
        for(UrlEntity urlEntity: urlEntities){
            urlEntityResponses.add(new UrlEntityResponse(urlEntity.getFullUrl(), urlEntity.getShortUrl()));
        }
        return urlEntityResponses;
    }

}