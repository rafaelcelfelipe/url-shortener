package com.urlshortener.shortener.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urlshortener.shortener.model.ShortLink;

public interface ShortLinkRepository extends JpaRepository<ShortLink, String>{
}