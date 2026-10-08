package com.urlshortener.shortener.service;

import org.springframework.stereotype.Service;
import com.urlshortener.shortener.model.ShortLink;
import com.urlshortener.shortener.exception.InvalidUrlException;
import com.urlshortener.shortener.exception.ShortLinkNotFoundException;
import com.urlshortener.shortener.repository.ShortLinkRepository;
import com.urlshortener.shortener.mapper.ShortLinkMapper;
import com.urlshortener.shortener.dto.ShortLinkRequest;
import com.urlshortener.shortener.dto.ShortLinkResponse;
import org.springframework.beans.factory.annotation.Value;

import java.security.SecureRandom;
import java.net.URI;

@Service
public class ShortLinkService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGHT = 7;

    private final SecureRandom random = new SecureRandom();
    private final ShortLinkRepository repository;
    private final ShortLinkMapper mapper;
    private final String baseUrl;
    public ShortLinkService(ShortLinkRepository repository,
                            ShortLinkMapper mapper,
                            @Value("${shortener.base-url}") String baseUrl) {
        this.repository = repository;
        this.mapper = mapper;
        this.baseUrl = baseUrl;
    }
    public ShortLinkResponse create(ShortLinkRequest request) {
        String url = normalizeUrl(request.url());
        ShortLink saved = repository.save(new ShortLink(generateCode(), url));
        return mapper.toResponse(saved, baseUrl);
    }

    public ShortLinkResponse edit(ShortLinkRequest request, String code){
        String url = normalizeUrl(request.url());
        ShortLink existing = getShortLinkEntity(code);
        existing.setOriginalUrl(url);
        ShortLink saved = repository.save(existing);
        return mapper.toResponse(saved, baseUrl);
    }


    public String resolve(String code) {
        return repository.findById(code)
            .map(ShortLink::getOriginalUrl)
            .orElseThrow(() -> new ShortLinkNotFoundException(code));
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

    private ShortLink getShortLinkEntity(String code) {
        return repository.findById(code)
            .orElseThrow(() -> new ShortLinkNotFoundException(code));
    }
}