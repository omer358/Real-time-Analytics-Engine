package com.example.ingestionservice.service;

import com.example.ingestionservice.model.PurchaseEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

class PurchaseEventProducerTest {

    @Test
    @DisplayName("Should call KafkaTemplate to send event")
    void send_shouldCallKafkaTemplate() {
        // Arrange
        KafkaTemplate<String, PurchaseEvent> kafkaTemplate = mock(KafkaTemplate.class);
        PurchaseEventProducer producer = new PurchaseEventProducer(kafkaTemplate);
        PurchaseEvent event = PurchaseEvent.builder()
                .orderId("123")
                .productId("p1")
                .customerId("c1")
                .amount(50.0)
                .build();

        CompletableFuture<SendResult<String, PurchaseEvent>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any(PurchaseEvent.class)))
                .thenReturn(future);

        // Act
        producer.send(event);

        // Assert
        verify(kafkaTemplate).send(eq("purchase-events"), eq("123"), eq(event));
    }
}