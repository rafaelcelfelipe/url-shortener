package com.urlshortener.statistics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "processed_redirects")
public class ProcessedRedirect {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    protected ProcessedRedirect() {
    }

    public ProcessedRedirect(UUID eventId) {
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }
}