package com.example.processingservice.entity;

import jakarta.persistence.*;

import java.time.Instant;

// entity/PurchaseMetric.java
@Entity
@Table(name = "purchase_metrics")
public class PurchaseMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String userId;

    private int quantity;
    private double totalAmount;

    private Instant eventTime;   // From PurchaseEvent
    private Instant processedAt; // When analytics-service processed it
}
