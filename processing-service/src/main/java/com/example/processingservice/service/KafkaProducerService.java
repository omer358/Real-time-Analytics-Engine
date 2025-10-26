package com.example.processingservice.service;

import com.example.processingservice.model.FlatOrderProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, FlatOrderProduct> kafkaTemplate;

    public void sendFlatOrder(FlatOrderProduct flatOrderProduct) {
        kafkaTemplate.send("order-placed-flat-events", flatOrderProduct.getKey(), flatOrderProduct);
    }
}
