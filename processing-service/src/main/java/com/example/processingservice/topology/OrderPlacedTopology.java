package com.example.processingservice.topology;

import com.example.commonlib.events.FlatOrderProduct;
import com.example.commonlib.events.OrderPlacedEvent;
import com.example.commonlib.topics.KafkaTopics;
import com.example.processingservice.validation.OrderPlacedEventValidator;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.stream.Collectors;

@Configuration
public class OrderPlacedTopology {

    private final OrderPlacedEventValidator validator;

    public OrderPlacedTopology(OrderPlacedEventValidator validator) {
        this.validator = validator;
    }

    @Bean
    public KStream<String, FlatOrderProduct> orderPlacedStream(StreamsBuilder builder) {

        JsonSerde<OrderPlacedEvent> orderSerde = new JsonSerde<>(OrderPlacedEvent.class);
        JsonSerde<FlatOrderProduct> flatSerde = new JsonSerde<>(FlatOrderProduct.class);

        KStream<String, OrderPlacedEvent> source =
                builder.stream(KafkaTopics.ORDER_PLACED_EVENTS,
                        Consumed.with(Serdes.String(), orderSerde));

        KStream<String, OrderPlacedEvent> validOrders = source
                .filter((key, order) -> validator.validate(order));

        KStream<String, FlatOrderProduct> flatStream = validOrders.flatMapValues(order ->
                order.getProducts().stream()
                .map(p -> new FlatOrderProduct(
                        order.getOrderId(),
                        order.getCustomerId(),
                        order.getTotalAmount(),
                        order.getCreatedAt().toEpochMilli(),
                        p.getProductId(),
                        p.getQuantity(),
                        p.getPrice(),
                        order.getOrderId() + "-" + p.getProductId()
                ))
                .collect(Collectors.toList())
        );

        flatStream.to(KafkaTopics.ORDER_PLACED_FLAT_EVENTS,
                Produced.with(Serdes.String(), flatSerde));

        return flatStream;
    }
}
