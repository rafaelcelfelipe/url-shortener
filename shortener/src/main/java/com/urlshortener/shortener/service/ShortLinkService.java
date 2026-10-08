package com.urlshortener.shortener.service;

import org.springframework.stereotype.Service;
import com.urlshortener.shortener.model.ShortLink;
import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.repository.ShortLinkRepository;
import com.urlshortener.shortener.mapper.ShortLinkMapper;
import com.urlshortener.shortener.dto.CreateLinkRequest;
import com.urlshortener.shortener.dto.CreateLinkResponse;

import java.security.SecureRandom;
import java.net.URI;

@Service
public class ShortLinkService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGHT = 7;

    private final SecureRandom random = new SecureRandom();
    private final ShortLinkRepository repository;
    private final ShortLinkMapper mapper;

    public ShortLinkService(ShortLinkRepository repository, ShortLinkMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    public CreateLinkResponse create(CreateLinkRequest request){
        String url = normalizeUrl(request.url());
        ShortLink saved = repository.save(new ShortLink(generateCode(), url));
        return mapper.toResponse(saved);
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