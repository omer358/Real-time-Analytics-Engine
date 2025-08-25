package com.example.ingestionservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class PurchaseEvent {
    private String orderId;
    private String productId;
    private String customerId;
    private double amount;
    private Instant timestamp;
}
