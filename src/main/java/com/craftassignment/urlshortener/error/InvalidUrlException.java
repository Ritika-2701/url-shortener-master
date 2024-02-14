package com.craftassignment.urlshortener.error;

import lombok.Getter;

@Getter
public class InvalidUrlException extends Exception {
    private String field;
    private String value;
    private String message;

    public InvalidUrlException(String field, String value, String message) {
        this.field = field;
        this.value = value;
        this.message = message;
    }
    public InvalidUrlException(String message) {
        this.message = message;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
