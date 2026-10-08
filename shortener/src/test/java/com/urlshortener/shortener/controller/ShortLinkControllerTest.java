package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.dto.ShortLinkRequest;
import com.urlshortener.shortener.dto.ShortLinkResponse;
import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.service.ShortLinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
            .thenReturn(new ShortLinkResponse("abc1234", "http://localhost:8080/abc1234"));
        
        mockMvc.perform(post("/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("abc1234"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc1234"));
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
}