package com.urlshortener.statistics.repository;

import com.urlshortener.statistics.model.LinkAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkAccessRepository extends JpaRepository<LinkAccess, String> {
}