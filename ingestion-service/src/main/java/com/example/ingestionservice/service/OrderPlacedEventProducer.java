package com.example.ingestionservice.service;

import com.example.commonlib.events.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OrderPlacedEventProducer {

    private static final String TOPIC = "order-placed-events";
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderPlacedEventProducer(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(OrderPlacedEvent event) {
        CompletableFuture<SendResult<String, OrderPlacedEvent>> future =
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
