package com.urlshortener.statistics.controller;

import com.urlshortener.statistics.dto.AccessCountResponse;
import com.urlshortener.statistics.service.AccessCountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessCountController.class)
class AccessCountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccessCountService service;

    @Test
    void returnsAccessCount() throws Exception {
        when(service.get("abc1234")).thenReturn(new AccessCountResponse("abc1234", 3));

        mockMvc.perform(get("/links/abc1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("abc1234"))
                .andExpect(jsonPath("$.accessCount").value(3));
    }

    @Test
    void returnsZeroWhenThereAreNoAccesses() throws Exception {
        when(service.get("missing")).thenReturn(new AccessCountResponse("missing", 0));

        mockMvc.perform(get("/links/missing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("missing"))
                .andExpect(jsonPath("$.accessCount").value(0));
    }
}
