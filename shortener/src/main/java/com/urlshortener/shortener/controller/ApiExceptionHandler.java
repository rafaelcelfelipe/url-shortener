package com.urlshortener.shortener.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import com.urlshortener.shortener.exception.InvalidUrlException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Map<String, String>> handleInvalidUrl(InvalidUrlException exception){
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
}