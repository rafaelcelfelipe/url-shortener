package com.urlshortener.statistics.listener;

import com.urlshortener.statistics.event.LinkRedirected;
import com.urlshortener.statistics.service.AccessCountService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LinkRedirectedListenerTest {

    @Test
    void delegatesToTheService() {
        AccessCountService service = mock(AccessCountService.class);
        LinkRedirectedListener listener = new LinkRedirectedListener(service);
        LinkRedirected event = new LinkRedirected(UUID.randomUUID(), "abc1234", Instant.parse("2026-10-15T18:00:00Z"));

        listener.onRedirect(event);

        verify(service).register(event);
    }
}
