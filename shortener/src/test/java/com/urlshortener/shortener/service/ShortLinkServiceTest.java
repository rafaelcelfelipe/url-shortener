package com.urlshortener.shortener.service;

import com.urlshortener.shortener.dto.ShortLinkRequest;
import com.urlshortener.shortener.dto.ShortLinkResponse;
import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.exception.ShortLinkNotFoundException;
import com.urlshortener.shortener.mapper.ShortLinkMapper;
import com.urlshortener.shortener.model.ShortLink;
import com.urlshortener.shortener.repository.ShortLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class ShortLinkServiceTest {

    @Mock
    private ShortLinkRepository repository;

    private ShortLinkService service;

    @BeforeEach
    void setUp() {
        ShortLinkMapper mapper = Mappers.getMapper(ShortLinkMapper.class);
        service = new ShortLinkService(repository, mapper, "http://localhost:8080");
    }

    @Test
    void createsLinkForHttpsUrl() {
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLinkResponse response = service.create(new ShortLinkRequest("https://example.com/path"));

        assertEquals("http://localhost:8080/" + response.code(), response.shortUrl());
        assertEquals(7, response.code().length());
        assertTrue(response.code().matches("[0-9a-zA-Z]{7}"));
    }

    @Test
    void acceptsHttpUrl() {
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLinkResponse response = service.create(new ShortLinkRequest("http://example.com"));

        assertEquals("http://localhost:8080/" + response.code(), response.shortUrl());
    }

    @Test
    void editsExistingLinkAndKeepsTheCode() {
        when(repository.findById("abc1234")).thenReturn(Optional.of(new ShortLink("abc1234", "https://example.com/old")));
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLinkResponse response = service.edit(new ShortLinkRequest("https://example.com/new"), "abc1234");

        ArgumentCaptor<ShortLink> saved = ArgumentCaptor.forClass(ShortLink.class);
        verify(repository).save(saved.capture());
        assertEquals("abc1234", saved.getValue().getCode());
        assertEquals("https://example.com/new", saved.getValue().getOriginalUrl());
        assertEquals("abc1234", response.code());
        assertEquals("http://localhost:8080/abc1234", response.shortUrl());
    }

    @Test
    void rejectsEditOfUnknownCode() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ShortLinkNotFoundException.class,
                () -> service.edit(new ShortLinkRequest("https://example.com"), "missing"));
    }

    @Test
    void rejectsEditWithInvalidUrl() {
        assertThrows(InvalidUrlException.class,
                () -> service.edit(new ShortLinkRequest("ftp://example.com"), "abc1234"));
    }

    @Test
    void resolvesExistingCode() {
        when(repository.findById("abc1234")).thenReturn(Optional.of(new ShortLink("abc1234", "https://example.com/path")));

        assertEquals("https://example.com/path", service.resolve("abc1234"));
    }

    @Test
    void rejectsUnknownCode() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ShortLinkNotFoundException.class, () -> service.resolve("missing"));
    }

    @Test
    void rejectsBlankUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create(new ShortLinkRequest("   ")));
    }

    @Test
    void rejectsRelativeUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create(new ShortLinkRequest("/caminho")));
    }

    @Test
    void rejectsUrlWithoutHost() {
        assertThrows(InvalidUrlException.class, () -> service.create(new ShortLinkRequest("https:///nohost")));
    }

    @Test
    void rejectsNonHttpScheme() {
        assertThrows(InvalidUrlException.class, () -> service.create(new ShortLinkRequest("javascript:alert(1)")));
        assertThrows(InvalidUrlException.class, () -> service.create(new ShortLinkRequest("ftp://example.com")));
    }

    @Test
    void rejectsMalformedUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create(new ShortLinkRequest("https://ex ample.com")));
    }
}
