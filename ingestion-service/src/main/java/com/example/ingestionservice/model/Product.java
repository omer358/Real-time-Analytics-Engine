package com.example.ingestionservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    @NotBlank(message = "Product ID is required")
    private String productId;

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    @Positive(message = "Price must be greater than zero")
    private double price;
}