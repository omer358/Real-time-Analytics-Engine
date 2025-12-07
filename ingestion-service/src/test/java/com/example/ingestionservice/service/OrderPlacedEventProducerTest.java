package com.example.ingestionservice.service;

import com.example.commonlib.events.OrderPlacedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class OrderPlacedEventProducerTest {

    @Test
    @DisplayName("Should call KafkaTemplate to send event")
    void send_shouldCallKafkaTemplate() {
        // Arrange
        KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        OrderPlacedEventProducer producer = new OrderPlacedEventProducer(kafkaTemplate);
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId("123")
                .customerId("c1")
                .totalAmount(50.0)
                .build();

        CompletableFuture<SendResult<String, OrderPlacedEvent>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderPlacedEvent.class)))
                .thenReturn(future);

        // Act
        producer.send(event);

        // Assert
        verify(kafkaTemplate).send(eq("order-placed-events"), eq("123"), eq(event));
    }
}