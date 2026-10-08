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
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
        assertEquals("https://example.com/path", response.originalUrl());
        assertEquals(7, response.code().length());
        assertTrue(response.code().matches("[0-9a-zA-Z]{7}"));
    }

    @Test
    void acceptsHttpUrl() {
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortLinkResponse response = service.create(new ShortLinkRequest("http://example.com"));

        assertEquals("http://localhost:8080/" + response.code(), response.shortUrl());
        assertEquals("http://example.com", response.originalUrl());
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
        assertEquals("https://example.com/new", response.originalUrl());
    }

    @Test
    void listsAllLinks() {
        when(repository.findAll()).thenReturn(List.of(
                new ShortLink("abc1234", "https://example.com"),
                new ShortLink("xyz9876", "https://example.org/path")));

        List<ShortLinkResponse> response = service.getAll();

        assertEquals(2, response.size());
        assertEquals("abc1234", response.get(0).code());
        assertEquals("http://localhost:8080/abc1234", response.get(0).shortUrl());
        assertEquals("https://example.com", response.get(0).originalUrl());
        assertEquals("xyz9876", response.get(1).code());
        assertEquals("http://localhost:8080/xyz9876", response.get(1).shortUrl());
        assertEquals("https://example.org/path", response.get(1).originalUrl());
    }

    @Test
    void listsNothingWhenRepositoryIsEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.getAll().isEmpty());
    }

    @Test
    void deletesExistingLink() {
        ShortLink existing = new ShortLink("abc1234", "https://example.com");
        when(repository.findById("abc1234")).thenReturn(Optional.of(existing));

        service.delete("abc1234");

        verify(repository).delete(existing);
    }

    @Test
    void rejectsDeleteOfUnknownCode() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ShortLinkNotFoundException.class, () -> service.delete("missing"));
        verify(repository, never()).delete(any());
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
