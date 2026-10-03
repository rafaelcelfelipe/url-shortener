package com.urlshortener.shortener.service;

import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.model.ShortLink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShortLinkServiceTest {

    private final ShortLinkService service = new ShortLinkService();

    @Test
    void createsLinkForHttpsUrl() {
        ShortLink link = service.create("https://example.com/path");

        assertEquals("https://example.com/path", link.getOriginalUrl());
        assertEquals(7, link.getCode().length());
        assertTrue(link.getCode().matches("[0-9a-zA-Z]{7}"));
    }

    @Test
    void acceptsHttpUrl() {
        ShortLink link = service.create("http://example.com");

        assertEquals("http://example.com", link.getOriginalUrl());
    }

    @Test
    void rejectsBlankUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create("   "));
    }

    @Test
    void rejectsRelativeUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create("/caminho"));
    }

    @Test
    void rejectsUrlWithoutHost() {
        assertThrows(InvalidUrlException.class, () -> service.create("https:///nohost"));
    }

    @Test
    void rejectsNonHttpScheme() {
        assertThrows(InvalidUrlException.class, () -> service.create("javascript:alert(1)"));
        assertThrows(InvalidUrlException.class, () -> service.create("ftp://example.com"));
    }

    @Test
    void rejectsMalformedUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create("https://ex ample.com"));
    }
}