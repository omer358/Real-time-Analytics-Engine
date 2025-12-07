package com.example.commonlib.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatOrderProduct {
    private String orderId;
    private String customerId;
    private double totalAmount;
    private long timestamp;
    private String productId;
    private int quantity;
    private double price;
    private String key;
}
