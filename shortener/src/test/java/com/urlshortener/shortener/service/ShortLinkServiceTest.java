package com.urlshortener.shortener.service;

import com.urlshortener.shortener.dto.CreateLinkRequest;
import com.urlshortener.shortener.dto.CreateLinkResponse;
import com.urlshortener.shortener.exception.InvalidUrlException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortLinkServiceTest {

    @Mock
    private ShortLinkRepository repository;

    private ShortLinkService service;

    @BeforeEach
    void setUp() {
        ShortLinkMapper mapper = Mappers.getMapper(ShortLinkMapper.class);
        service = new ShortLinkService(repository, mapper);
    }

    @Test
    void createsLinkForHttpsUrl() {
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateLinkResponse response = service.create(new CreateLinkRequest("https://example.com/path"));

        assertEquals("https://example.com/path", response.originalUrl());
        assertEquals(7, response.code().length());
        assertTrue(response.code().matches("[0-9a-zA-Z]{7}"));
    }

    @Test
    void acceptsHttpUrl() {
        when(repository.save(any(ShortLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateLinkResponse response = service.create(new CreateLinkRequest("http://example.com"));

        assertEquals("http://example.com", response.originalUrl());
    }

    @Test
    void rejectsBlankUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create(new CreateLinkRequest("   ")));
    }

    @Test
    void rejectsRelativeUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create(new CreateLinkRequest("/caminho")));
    }

    @Test
    void rejectsUrlWithoutHost() {
        assertThrows(InvalidUrlException.class, () -> service.create(new CreateLinkRequest("https:///nohost")));
    }

    @Test
    void rejectsNonHttpScheme() {
        assertThrows(InvalidUrlException.class, () -> service.create(new CreateLinkRequest("javascript:alert(1)")));
        assertThrows(InvalidUrlException.class, () -> service.create(new CreateLinkRequest("ftp://example.com")));
    }

    @Test
    void rejectsMalformedUrl() {
        assertThrows(InvalidUrlException.class, () -> service.create(new CreateLinkRequest("https://ex ample.com")));
    }
}