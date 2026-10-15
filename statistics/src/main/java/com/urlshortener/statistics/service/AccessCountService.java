package com.urlshortener.statistics.service;

import com.urlshortener.statistics.dto.AccessCountResponse;
import com.urlshortener.statistics.event.LinkRedirected;
import com.urlshortener.statistics.mapper.LinkAccessMapper;
import com.urlshortener.statistics.model.LinkAccess;
import com.urlshortener.statistics.repository.LinkAccessRepository;
import com.urlshortener.statistics.repository.ProcessedRedirectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessCountService {

    private final LinkAccessRepository accesses;
    private final ProcessedRedirectRepository processed;
    private final LinkAccessMapper mapper;

    public AccessCountService(LinkAccessRepository accesses,
                              ProcessedRedirectRepository processed,
                              LinkAccessMapper mapper) {
        this.accesses = accesses;
        this.processed = processed;
        this.mapper = mapper;
    }

    @Transactional
    public void register(LinkRedirected event) {
        int inserted = processed.insertIfAbsent(event.eventId());
        if (inserted == 0) {
            return;
        }
        LinkAccess access = accesses.findById(event.code())
                .orElseGet(() -> new LinkAccess(event.code(), 0));
        access.setAccessCount(access.getAccessCount() + 1);
        accesses.save(access);
    }

    public AccessCountResponse get(String code) {
        return accesses.findById(code)
                .map(mapper::toResponse)
                .orElseGet(() -> new AccessCountResponse(code, 0));
    }
}