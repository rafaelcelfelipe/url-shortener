package com.urlshortener.shortener.model;


public class ShortLink {
    private String code;
    private String originalUrl;

    public ShortLink(String code, String originalUrl){
        this.code = code;
        this.originalUrl = originalUrl;
    }

    public String getCode(){
        return code;
    }

    public String getOriginalUrl(){
        return originalUrl;
    }

    public void setCode(String code){
        this.code = code;
    }

    public void setOriginalUrl(String originalUrl){
        this.originalUrl = originalUrl;
    }


}