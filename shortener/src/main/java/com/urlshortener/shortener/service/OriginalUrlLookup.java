package com.urlshortener.shortener.service;

import com.urlshortener.shortener.exception.ShortLinkNotFoundException;
import com.urlshortener.shortener.model.ShortLink;
import com.urlshortener.shortener.repository.ShortLinkRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class OriginalUrlLookup {

    public static final String CACHE_NAME = "original-urls";

    private final ShortLinkRepository repository;

    public OriginalUrlLookup(ShortLinkRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#code")
    public String findOriginalUrl(String code) {
        return repository.findById(code)
                .map(ShortLink::getOriginalUrl)
                .orElseThrow(() -> new ShortLinkNotFoundException(code));
    }

    @CacheEvict(cacheNames = CACHE_NAME, key = "#code")
    public void evict(String code) {
    }
}