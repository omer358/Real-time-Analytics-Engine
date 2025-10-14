package com.example.analyticsservice.consumer;

import com.example.analyticsservice.model.OrderPlacedEvent;
import com.example.analyticsservice.validation.OrderPlacedEventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderPlacedEventConsumer {
    private final OrderPlacedEventValidator validator;
    public OrderPlacedEventConsumer(OrderPlacedEventValidator validator) {
        this.validator = validator;
    }

    @KafkaListener(topics = "order-placed-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderPlacedEvent event) {
        boolean valid = validator.validate(event);
        if (valid) {
            log.info("Event received: {}", event);
        } else {
            log.warn("Invalid event received: {}", event);
        }
    }
}
