package com.example.processingservice.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Positive(message = "Total amount must be greater than zero")
    private double totalAmount;

    @NotNull(message = "Timestamp is required")
    private Instant timestamp;

    @NotEmpty(message = "Order must have at least one product")
    @Valid
    private List<Product> products;

}