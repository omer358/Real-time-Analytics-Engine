package com.example.ingestionservice.config;

import com.example.ingestionservice.model.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class KafkaProducerConfigTest {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Test
    void kafkaTemplate_shouldNotBeNull() {
        assertNotNull(kafkaTemplate, "KafkaTemplate should be created");
    }

    @Test
    void producerFactory_shouldCreateProducer() {
        assertNotNull(kafkaTemplate.getProducerFactory());
    }
}
