package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.service.ShortLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;


@RestController
public class RedirectController {
    private final ShortLinkService service;

    public RedirectController(ShortLinkService service) {
        this.service = service;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        URI location = URI.create(service.resolve(code));
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    }


}