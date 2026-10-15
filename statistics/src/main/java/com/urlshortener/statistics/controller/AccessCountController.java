package com.urlshortener.statistics.controller;

import com.urlshortener.statistics.dto.AccessCountResponse;
import com.urlshortener.statistics.service.AccessCountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class AccessCountController {

    private final AccessCountService service;

    public AccessCountController(AccessCountService service) {
        this.service = service;
    }

    @GetMapping("/{code}")
    public AccessCountResponse get(@PathVariable String code) {
        return service.get(code);
    }
}