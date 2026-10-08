package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.dto.CreateLinkRequest;
import com.urlshortener.shortener.dto.CreateLinkResponse;
import com.urlshortener.shortener.service.ShortLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class ShortLinkController {
    private final ShortLinkService service;

    public ShortLinkController(ShortLinkService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateLinkResponse> create(@RequestBody CreateLinkRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
}