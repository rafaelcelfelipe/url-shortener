package com.urlshortener.shortener.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name = "short_links")
public class ShortLink {
    @Id
    private String code;
    @Column(nullable = false, length = 2048)
    private String originalUrl;

    protected ShortLink(){
    }

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