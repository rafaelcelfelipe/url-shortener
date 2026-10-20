package com.urlshortener.shortener.service;

import com.urlshortener.shortener.exception.ShortLinkNotFoundException;
import com.urlshortener.shortener.model.ShortLink;
import com.urlshortener.shortener.repository.ShortLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(OriginalUrlLookupCacheTest.Config.class)
class OriginalUrlLookupCacheTest {

    @Autowired
    private OriginalUrlLookup lookup;

    @Autowired
    private ShortLinkRepository repository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        reset(repository);
        cacheManager.getCache(OriginalUrlLookup.CACHE_NAME).clear();
    }

    @Test
    void readsTheDatabaseOnceForTheSameCode() {
        when(repository.findById("abc1234")).thenReturn(Optional.of(new ShortLink("abc1234", "https://example.com")));

        assertEquals("https://example.com", lookup.findOriginalUrl("abc1234"));
        assertEquals("https://example.com", lookup.findOriginalUrl("abc1234"));

        verify(repository, times(1)).findById("abc1234");
    }

    @Test
    void readsAgainAfterEvict() {
        when(repository.findById("abc1234"))
                .thenReturn(Optional.of(new ShortLink("abc1234", "https://example.com/old")))
                .thenReturn(Optional.of(new ShortLink("abc1234", "https://example.com/new")));

        assertEquals("https://example.com/old", lookup.findOriginalUrl("abc1234"));
        lookup.evict("abc1234");

        assertEquals("https://example.com/new", lookup.findOriginalUrl("abc1234"));
        verify(repository, times(2)).findById("abc1234");
    }

    @Test
    void doesNotCacheAMissingCode() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ShortLinkNotFoundException.class, () -> lookup.findOriginalUrl("missing"));
        assertThrows(ShortLinkNotFoundException.class, () -> lookup.findOriginalUrl("missing"));

        verify(repository, times(2)).findById("missing");
    }

    @Configuration
    @EnableCaching
    static class Config {

        @Bean
        ShortLinkRepository repository() {
            return mock(ShortLinkRepository.class);
        }

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(OriginalUrlLookup.CACHE_NAME);
        }

        @Bean
        OriginalUrlLookup originalUrlLookup(ShortLinkRepository repository) {
            return new OriginalUrlLookup(repository);
        }
    }
}
