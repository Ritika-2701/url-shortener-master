package com.craftassignment.urlshortener.dto;

import jdk.internal.jline.internal.Nullable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class URLRequestDTO {
    public FullUrl fullUrl;
    @Nullable
    public String customUrl;

}
