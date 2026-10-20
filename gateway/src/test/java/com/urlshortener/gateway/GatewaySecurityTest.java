package com.urlshortener.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://127.0.0.1:9/certs")
@AutoConfigureMockMvc
class GatewaySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createLinkRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void editLinkRequiresAuthentication() throws Exception {
        mockMvc.perform(put("/links/abc1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/new\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteLinkRequiresAuthentication() throws Exception {
        mockMvc.perform(delete("/links/abc1234"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedCreatePassesTheGatewayCheck() throws Exception {
        mockMvc.perform(post("/links")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()));
    }

    @Test
    void redirectStaysPublic() throws Exception {
        mockMvc.perform(get("/abc1234"))
                .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()));
    }

    @Test
    void linkListStaysPublic() throws Exception {
        mockMvc.perform(get("/links"))
                .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()));
    }

    @Test
    void accessCountStaysPublic() throws Exception {
        mockMvc.perform(get("/statistics/links/abc1234"))
                .andExpect(result -> assertNotEquals(401, result.getResponse().getStatus()));
    }
}
