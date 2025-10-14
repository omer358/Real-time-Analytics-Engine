package com.example.processingservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @NotBlank(message = "Product ID is required")
    private String productId;

    @Positive(message = "Quantity must be greater than zero")
    private int quantity;

    @Positive(message = "Price must be greater than zero")
    private double price;
}