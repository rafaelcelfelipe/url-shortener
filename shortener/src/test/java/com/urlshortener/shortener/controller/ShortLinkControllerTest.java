package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.dto.ShortLinkRequest;
import com.urlshortener.shortener.dto.ShortLinkResponse;
import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.exception.ShortLinkNotFoundException;
import com.urlshortener.shortener.service.ShortLinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortLinkController.class)
class ShortLinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortLinkService service;

    @Test
    void createsLink() throws Exception {
        when(service.create(new ShortLinkRequest("https://example.com")))
            .thenReturn(new ShortLinkResponse("abc1234", "http://localhost:8080/abc1234", "https://example.com"));

        mockMvc.perform(post("/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("abc1234"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc1234"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));
    }

    @Test
    void rejectsInvalidUrl() throws Exception {
        when(service.create(new ShortLinkRequest("ftp://example.com")))
                .thenThrow(new InvalidUrlException("Url must start with http or https"));

        mockMvc.perform(post("/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Url must start with http or https"));
    }

    @Test
    void editsLink() throws Exception {
        when(service.edit(new ShortLinkRequest("https://example.com/new"), "abc1234"))
                .thenReturn(new ShortLinkResponse("abc1234", "http://localhost:8080/abc1234", "https://example.com/new"));

        mockMvc.perform(put("/links/abc1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/new\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("abc1234"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc1234"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/new"));
    }

    @Test
    void rejectsEditOfUnknownCode() throws Exception {
        when(service.edit(new ShortLinkRequest("https://example.com"), "missing"))
                .thenThrow(new ShortLinkNotFoundException("missing"));

        mockMvc.perform(put("/links/missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("short link not found: missing"));
    }

    @Test
    void rejectsEditWithInvalidUrl() throws Exception {
        when(service.edit(new ShortLinkRequest("ftp://example.com"), "abc1234"))
                .thenThrow(new InvalidUrlException("Url must start with http or https"));

        mockMvc.perform(put("/links/abc1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"ftp://example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Url must start with http or https"));
    }

    @Test
    void listsLinks() throws Exception {
        when(service.getAll()).thenReturn(List.of(
                new ShortLinkResponse("abc1234", "http://localhost:8080/abc1234", "https://example.com"),
                new ShortLinkResponse("xyz9876", "http://localhost:8080/xyz9876", "https://example.org/path")));

        mockMvc.perform(get("/links"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].code").value("abc1234"))
                .andExpect(jsonPath("$[0].shortUrl").value("http://localhost:8080/abc1234"))
                .andExpect(jsonPath("$[0].originalUrl").value("https://example.com"))
                .andExpect(jsonPath("$[1].code").value("xyz9876"))
                .andExpect(jsonPath("$[1].shortUrl").value("http://localhost:8080/xyz9876"))
                .andExpect(jsonPath("$[1].originalUrl").value("https://example.org/path"));
    }

    @Test
    void listsNothingWhenThereAreNoLinks() throws Exception {
        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/links"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void deletesLink() throws Exception {
        mockMvc.perform(delete("/links/abc1234"))
                .andExpect(status().isNoContent());

        verify(service).delete("abc1234");
    }

    @Test
    void rejectsDeleteOfUnknownCode() throws Exception {
        doThrow(new ShortLinkNotFoundException("missing")).when(service).delete("missing");

        mockMvc.perform(delete("/links/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("short link not found: missing"));
    }
}