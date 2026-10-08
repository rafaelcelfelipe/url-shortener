package com.urlshortener.shortener.service;

import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.model.ShortLink;
import com.urlshortener.shortener.repository.ShortLinkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortLinkServiceTest {

    @Mock
    private ShortLinkRepository repository;

    @InjectMocks
    private ShortLinkService service;

    @Test
    void createsLinkForHttpsUrl() {
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ShortLink link = service.create("https://example.com/path");
        assertEquals("https://example.com/path", link.getOriginalUrl());
        assertEquals(7, link.getCode().length());
        assertTrue(link.getCode().matches("[0-9a-zA-Z]{7}"));
    }

    @Test
    void acceptsHttpUrl() {
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));
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