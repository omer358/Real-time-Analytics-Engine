package com.example.ingestionservice.controller;

import com.example.ingestionservice.model.PurchaseEvent;
import com.example.ingestionservice.service.PurchaseEventProducer;
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
public class PurchaseEventController {
    private final PurchaseEventProducer purchaseEventProducer;

    @PostMapping("/purchase")
    public ResponseEntity<String> sendEvent(@RequestBody @Valid PurchaseEvent payload) {
        purchaseEventProducer.send(payload);
        return ResponseEntity.ok("Event sent");
    }
}
