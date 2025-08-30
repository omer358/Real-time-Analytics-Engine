package com.example.processingservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseEvent {
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Positive(message = "Amount must be greater than zero")
    private double amount;

    @NotNull(message = "Timestamp is required")
    private Instant timestamp;
}