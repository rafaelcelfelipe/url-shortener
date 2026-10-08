package com.urlshortener.shortener.service;

import org.springframework.stereotype.Service;
import com.urlshortener.shortener.model.ShortLink;
import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.repository.ShortLinkRepository;

import java.security.SecureRandom;
import java.net.URI;

@Service
public class ShortLinkService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGHT = 7;

    private final SecureRandom random = new SecureRandom();
    private final ShortLinkRepository repository;

    public ShortLinkService(ShortLinkRepository repository){
        this.repository = repository;
    }

    public ShortLink create(String originalUrl){
        String url = normalizeUrl(originalUrl);
        return repository.save(new ShortLink(generateCode(), url));
    }

    private String normalizeUrl(String originalUrl){
        if (originalUrl == null || originalUrl.isBlank()){
            throw new InvalidUrlException("Url is required");
        }
        String trimmedUrl = originalUrl.trim();
        URI uri;
        try {
            uri = URI.create(trimmedUrl);
        } catch (IllegalArgumentException e) {
            throw new InvalidUrlException("Invalid URL: " + trimmedUrl, e);
        }
        String scheme = uri.getScheme();
        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)){
            throw new InvalidUrlException("Url must start with http or https");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()){
            throw new InvalidUrlException("Url must have a host");
        }
        return trimmedUrl;
    }

    private String generateCode(){
        StringBuilder code = new StringBuilder(CODE_LENGHT);
        for (int i = 0; i < CODE_LENGHT; i++){
            int index = random.nextInt(ALPHABET.length());
            code.append(ALPHABET.charAt(index));
        }
        return code.toString();
    }
}