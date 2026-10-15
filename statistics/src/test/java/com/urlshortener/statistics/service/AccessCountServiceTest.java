package com.urlshortener.statistics.service;

import com.urlshortener.statistics.dto.AccessCountResponse;
import com.urlshortener.statistics.event.LinkRedirected;
import com.urlshortener.statistics.mapper.LinkAccessMapper;
import com.urlshortener.statistics.model.LinkAccess;
import com.urlshortener.statistics.repository.LinkAccessRepository;
import com.urlshortener.statistics.repository.ProcessedRedirectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessCountServiceTest {

    @Mock
    private LinkAccessRepository accesses;

    @Mock
    private ProcessedRedirectRepository processed;

    private AccessCountService service;

    @BeforeEach
    void setUp() {
        LinkAccessMapper mapper = Mappers.getMapper(LinkAccessMapper.class);
        service = new AccessCountService(accesses, processed, mapper);
    }

    @Test
    void countsFirstRedirect() {
        UUID eventId = UUID.randomUUID();
        when(processed.insertIfAbsent(eventId)).thenReturn(1);
        when(accesses.findById("abc1234")).thenReturn(Optional.empty());
        when(accesses.save(any(LinkAccess.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.register(new LinkRedirected(eventId, "abc1234", Instant.parse("2026-10-15T18:00:00Z")));

        ArgumentCaptor<LinkAccess> saved = ArgumentCaptor.forClass(LinkAccess.class);
        verify(accesses).save(saved.capture());
        assertEquals("abc1234", saved.getValue().getCode());
        assertEquals(1, saved.getValue().getAccessCount());
    }

    @Test
    void incrementsExistingCount() {
        UUID eventId = UUID.randomUUID();
        when(processed.insertIfAbsent(eventId)).thenReturn(1);
        when(accesses.findById("abc1234")).thenReturn(Optional.of(new LinkAccess("abc1234", 4)));
        when(accesses.save(any(LinkAccess.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.register(new LinkRedirected(eventId, "abc1234", Instant.parse("2026-10-15T18:00:00Z")));

        ArgumentCaptor<LinkAccess> saved = ArgumentCaptor.forClass(LinkAccess.class);
        verify(accesses).save(saved.capture());
        assertEquals(5, saved.getValue().getAccessCount());
    }

    @Test
    void ignoresDuplicateEvent() {
        UUID eventId = UUID.randomUUID();
        when(processed.insertIfAbsent(eventId)).thenReturn(0);

        service.register(new LinkRedirected(eventId, "abc1234", Instant.parse("2026-10-15T18:00:00Z")));

        verify(accesses, never()).save(any());
    }

    @Test
    void returnsZeroWhenTheCodeWasNeverAccessed() {
        when(accesses.findById("missing")).thenReturn(Optional.empty());

        AccessCountResponse response = service.get("missing");

        assertEquals("missing", response.code());
        assertEquals(0, response.accessCount());
    }

    @Test
    void returnsStoredCount() {
        when(accesses.findById("abc1234")).thenReturn(Optional.of(new LinkAccess("abc1234", 3)));

        AccessCountResponse response = service.get("abc1234");

        assertEquals("abc1234", response.code());
        assertEquals(3, response.accessCount());
    }
}
