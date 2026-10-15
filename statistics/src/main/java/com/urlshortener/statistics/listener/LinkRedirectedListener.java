package com.urlshortener.statistics.listener;

import com.urlshortener.statistics.event.LinkRedirected;
import com.urlshortener.statistics.service.AccessCountService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LinkRedirectedListener {

    private final AccessCountService service;

    public LinkRedirectedListener(AccessCountService service) {
        this.service = service;
    }

    @KafkaListener(topics = "${statistics.kafka.redirect-topic}")
    public void onRedirect(LinkRedirected event) {
        service.register(event);
    }
}