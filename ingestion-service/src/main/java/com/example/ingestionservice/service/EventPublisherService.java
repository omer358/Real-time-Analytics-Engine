package com.example.ingestionservice.service;

import com.example.ingestionservice.config.KafkaProducerConfig;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {
    private static final String TOPIC = "PURCHASE_EVENT";
    private final KafkaProducerConfig kafkaProducerConfig;

    public EventPublisherService(KafkaProducerConfig kafkaProducerConfig) {
        this.kafkaProducerConfig = kafkaProducerConfig;
    }

    public void publishEvent(String event) {
        kafkaProducerConfig.send(TOPIC, event.getBytes());
    }
}
