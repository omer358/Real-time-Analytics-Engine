package com.example.ingestionservice.controller;

import com.example.ingestionservice.model.PurchaseEvent;
import com.example.ingestionservice.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/events")
@RequiredArgsConstructor
public class PurchaseEventController {
    private final EventPublisherService eventPublisherService;

    @PostMapping("/purchase")
    public ResponseEntity<String> sendEvent(@RequestBody PurchaseEvent payload) {
        eventPublisherService.publishEvent(payload.toString());
        return ResponseEntity.ok("Event sent");
    }
}
