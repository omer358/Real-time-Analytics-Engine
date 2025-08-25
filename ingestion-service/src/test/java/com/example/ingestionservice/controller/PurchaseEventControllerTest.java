package com.example.ingestionservice.controller;

import com.example.ingestionservice.model.PurchaseEvent;
import com.example.ingestionservice.service.EventPublisherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseEventController.class)
class PurchaseEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventPublisherService eventPublisherService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenValidInput_thenReturns200() throws Exception {
        Instant fixedTimestamp = Instant.parse("2025-08-25T20:59:55Z");

        PurchaseEvent event = PurchaseEvent.builder()
                .orderId("123")
                .productId("456")
                .customerId("789")
                .amount(99.99)
                .timestamp(fixedTimestamp)
                .build();

        String response = mockMvc.perform(post("/api/v1/events/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(response).contains("Event sent");
    }
}