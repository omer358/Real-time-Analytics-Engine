package com.example.processingservice.consumer;

import com.example.processingservice.model.PurchaseEvent;
import com.example.processingservice.validation.PurchaseEventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PurchaseEventConsumer {
    private final PurchaseEventValidator validator;
    public PurchaseEventConsumer(PurchaseEventValidator validator) {
        this.validator = validator;
    }

    @KafkaListener(topics = "purchase-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PurchaseEvent event) {
        boolean valid = validator.validate(event);
        if (valid) {
            log.info("Event received: {}", event);
        } else {
            log.warn("Invalid event received: {}", event);
        }

    }
}
