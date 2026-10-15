package com.urlshortener.shortener.event;

import java.time.Instant;
import java.util.UUID;

public record LinkRedirected(UUID eventId, String code, Instant occurredAt) {
}