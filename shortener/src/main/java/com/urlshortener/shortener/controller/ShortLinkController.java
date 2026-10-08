package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.dto.ShortLinkRequest;
import com.urlshortener.shortener.dto.ShortLinkResponse;
import com.urlshortener.shortener.service.ShortLinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequestMapping("/links")
public class ShortLinkController {
    private final ShortLinkService service;

    public ShortLinkController(ShortLinkService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ShortLinkResponse> create(@RequestBody ShortLinkRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{code}")
    public ResponseEntity<ShortLinkResponse> edit(@RequestBody ShortLinkRequest request, @PathVariable String code) {
        return ResponseEntity.ok(service.edit(request, code));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ShortLinkResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }


}