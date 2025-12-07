package com.example.ingestionservice.controller;

import com.example.commonlib.events.OrderPlacedEvent;
import com.example.ingestionservice.service.OrderPlacedEventProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/events")
@RequiredArgsConstructor
public class OrderPlacedController {
    private final OrderPlacedEventProducer orderPlacedEventProducer;

    @PostMapping("/order-placed")
    public ResponseEntity<String> sendEvent(@RequestBody @Valid OrderPlacedEvent payload) {
        orderPlacedEventProducer.send(payload);
        return ResponseEntity.ok("Event sent");
    }
}
