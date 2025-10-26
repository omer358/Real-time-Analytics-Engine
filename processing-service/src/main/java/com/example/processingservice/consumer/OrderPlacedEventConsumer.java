package com.example.processingservice.consumer;

import com.example.processingservice.model.FlatOrderProduct;
import com.example.processingservice.model.OrderPlacedEvent;
import com.example.processingservice.service.KafkaProducerService;
import com.example.processingservice.validation.OrderPlacedEventValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderPlacedEventConsumer {

    private final OrderPlacedEventValidator validator;
    private final KafkaProducerService producerService;

    public OrderPlacedEventConsumer(OrderPlacedEventValidator validator, KafkaProducerService producerService) {
        this.validator = validator;
        this.producerService = producerService;
    }

    @KafkaListener(topics = "order-placed-events", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderPlacedEvent event) {
        if (!validator.validate(event)) {
            log.warn("Invalid event received: {}", event);
            return;
        }

        log.info("Event received: {}", event);

        long epochMillis = event.getTimestamp().toEpochMilli();

        event.getProducts().forEach(product -> {
            FlatOrderProduct flat = new FlatOrderProduct(
                    event.getOrderId(),
                    event.getCustomerId(),
                    event.getTotalAmount(),
                    epochMillis,
                    product.getProductId(),
                    product.getQuantity(),
                    product.getPrice(),
                    event.getOrderId() + "-" + product.getProductId() // key
            );
            log.info("Adding new order product to flat order event: {}", flat);
            producerService.sendFlatOrder(flat);
            log.info("Order placed flat event sent to topic: {}", flat);
        });
    }
}
