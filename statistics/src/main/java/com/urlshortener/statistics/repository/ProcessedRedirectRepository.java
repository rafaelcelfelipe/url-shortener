package com.urlshortener.statistics.repository;

import com.urlshortener.statistics.model.ProcessedRedirect;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedRedirectRepository extends org.springframework.data.jpa.repository.JpaRepository<ProcessedRedirect, UUID> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "insert into processed_redirects (event_id) values (:eventId) on conflict do nothing", nativeQuery = true)
    int insertIfAbsent(@Param("eventId") UUID eventId);
}