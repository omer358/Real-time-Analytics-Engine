package com.example.ingestionservice.service;

import com.example.ingestionservice.model.PurchaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class PurchaseEventProducer {

    private final KafkaTemplate<String, PurchaseEvent> kafkaTemplate;
    private static final String TOPIC = "purchase-events";

    public PurchaseEventProducer(KafkaTemplate<String, PurchaseEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PurchaseEvent event) {
        CompletableFuture<SendResult<String, PurchaseEvent>> future =
                kafkaTemplate.send(TOPIC, event.getOrderId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error sending event: {}", event, ex);
            } else {
                log.info("Event sent: {}", event);
            }
        });
    }
}
