package com.example.ingestionservice.controller;

import com.example.ingestionservice.model.OrderPlacedEvent;
import com.example.ingestionservice.model.Product;
import com.example.ingestionservice.service.OrderPlacedEventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderPlacedController.class)
class OrderPlacedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderPlacedEventProducer orderPlacedEventProducer;

    @MockBean
    private Clock clock;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenValidInput_thenReturns200() throws Exception {
        Instant fixedTimestamp = Instant.parse("2025-08-25T20:59:55Z");
        Product product = Product.builder()
                .productId("12")
                .quantity(1)
                .price(99.99)
                .build();
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId("123")
                .customerId("789")
                .totalAmount(99.99)
                .products(List.of(product))
                .timestamp(fixedTimestamp)
                .build();

        String response = mockMvc.perform(post("/api/v1/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Event sent");
    }

    @Test
    void whenMissingOrderId_thenReturns400() throws Exception {
        // Arrange: Mock the clock to return a fixed timestamp
        Instant fixedTimestamp = Instant.parse("2025-08-25T20:59:55Z");
        when(clock.instant()).thenReturn(fixedTimestamp);

        Product product = Product.builder()
                .productId("12")
                .quantity(1)
                .price(99.99)
                .build();
        // Build the payload
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .customerId("789")
                .totalAmount(99.99)
                .products(List.of(product))
                .timestamp(Instant.parse("2025-08-26T12:00:00Z"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"message\":\"Validation failed\"," +
                                "\"timestamp\":\"2025-08-25T20:59:55Z\"," +
                                "\"status\":400," +
                                "\"errors\":{\"orderId\":\"Order ID is required\"}}")
                );
    }

    @Test
    void whenMissingProductId_thenReturns400() throws Exception {
        // Arrange: Mock the clock to return a fixed timestamp
        Instant fixedTimestamp = Instant.parse("2025-08-25T20:59:55Z");
        when(clock.instant()).thenReturn(fixedTimestamp);

        Product product = Product.builder()
                .quantity(1)
                .price(99.99)
                .build();

        // Build the payload
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId("123")
                .customerId("789")
                .totalAmount(99.99)
                .products(List.of(product))
                .timestamp(Instant.parse("2025-08-26T12:00:00Z"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Validation failed\"," +
                        "\"timestamp\":\"2025-08-25T20:59:55Z\"," +
                        "\"status\":400," +
                        "\"errors\":{\"productId\":\"Product ID is required\"}}")
                );
    }

    @Test
    void whenMissingCustomerId_thenReturns400() throws Exception {
        // Arrange: Mock the clock to return a fixed timestamp
        Instant fixedTimestamp = Instant.parse("2025-08-25T20:59:55Z");
        when(clock.instant()).thenReturn(fixedTimestamp);

        // Build the payload
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId("123")
                .totalAmount(99.99)
                .timestamp(Instant.parse("2025-08-26T12:00:00Z"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Validation failed\"," +
                        "\"timestamp\":\"2025-08-25T20:59:55Z\"," +
                        "\"status\":400," +
                        "\"errors\":{\"customerId\":\"Customer ID is required\"}}")
                );
    }

    @Test
    void whenInvalidAmount_thenReturns400() throws Exception {
        // Arrange: Mock the clock to return a fixed timestamp
        Instant fixedTimestamp = Instant.parse("2025-08-25T20:59:55Z");
        when(clock.instant()).thenReturn(fixedTimestamp);
        Product product = Product.builder()
                .productId("12")
                .quantity(1)
                .price(99.99)
                .build();

        // Build the payload
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId("123")
                .customerId("789")
                .totalAmount(-99.99)
                .products(List.of(product))
                .timestamp(Instant.parse("2025-08-26T12:00:00Z"))
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Validation failed\"," +
                        "\"timestamp\":\"2025-08-25T20:59:55Z\"," +
                        "\"status\":400," +
                        "\"errors\":{\"totalAmount\":\"Total amount must be greater than zero\"}}")
                );
    }

    @Test
    void whenMissingTimestamp_thenReturns400() throws Exception {
        // Arrange: Mock the clock to return a fixed timestamp
        Instant fixedTimestamp = Instant.parse("2025-08-25T20:59:55Z");
        when(clock.instant()).thenReturn(fixedTimestamp);

        // Build the payload
        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId("123")
                .customerId("789")
                .totalAmount(99.99)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/events/order-placed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Validation failed\"," +
                        "\"timestamp\":\"2025-08-25T20:59:55Z\"," +
                        "\"status\":400," +
                        "\"errors\":{\"timestamp\":\"Timestamp is required\"}}"));
    }
}