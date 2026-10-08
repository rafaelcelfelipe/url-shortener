package com.urlshortener.shortener.exception;

public class ShortLinkNotFoundException extends RuntimeException {

    public ShortLinkNotFoundException(String code) {
        super("short link not found: " + code);
    }
}