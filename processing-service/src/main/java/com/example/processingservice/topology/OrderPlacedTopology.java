package com.example.processingservice.topology;

import com.example.commonlib.events.FlatOrderProduct;
import com.example.commonlib.events.OrderPlacedEvent;
import com.example.commonlib.topics.KafkaTopics;
import com.example.processingservice.mapper.FlatOrderProductMapper;
import com.example.processingservice.validation.OrderPlacedEventValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@Slf4j
public class OrderPlacedTopology {

    private final OrderPlacedEventValidator validator;
    private final FlatOrderProductMapper mapper;

    public OrderPlacedTopology(
            OrderPlacedEventValidator validator,
            FlatOrderProductMapper mapper
    ) {
        this.validator = validator;
        this.mapper = mapper;
    }

    @Bean
    public KStream<String, FlatOrderProduct> orderPlacedStream(StreamsBuilder builder) {

        log.info("Initializing OrderPlaced topology");

        KStream<String, OrderPlacedEvent> source = orderPlacedSource(builder);
        KStream<String, OrderPlacedEvent> validOrders = validate(source);
        KStream<String, FlatOrderProduct> flatProducts = flatten(validOrders);

        publish(flatProducts);

        return flatProducts;
    }

    /* ========================
       Topology steps
       ======================== */

    private KStream<String, OrderPlacedEvent> orderPlacedSource(
            StreamsBuilder builder
    ) {
        return builder
                .stream(
                        KafkaTopics.ORDER_PLACED_EVENTS,
                        Consumed.with(Serdes.String(), orderSerde())
                )
                .peek((k, v) ->
                        log.info(
                                "Order received | orderId={} customerId={} products={}",
                                v.getOrderId(),
                                v.getCustomerId(),
                                v.getProducts().size()
                        )
                );
    }

    private KStream<String, OrderPlacedEvent> validate(
            KStream<String, OrderPlacedEvent> source
    ) {
        return source.filter((key, order) -> {
            boolean valid = validator.validate(order);

            if (!valid) {
                log.warn(
                        "Invalid order dropped | orderId={}",
                        order.getOrderId()
                );
            }

            return valid;
        });
    }

    private KStream<String, FlatOrderProduct> flatten(
            KStream<String, OrderPlacedEvent> orders
    ) {
        return orders.flatMapValues(order -> {
            log.debug(
                    "Flattening order | orderId={} products={}",
                    order.getOrderId(),
                    order.getProducts().size()
            );

            return order.getProducts().stream()
                    .map(p -> {
                        FlatOrderProduct flat =
                                mapper.map(order, p);

                        log.debug(
                                "Flat product created | orderId={} productId={} quantity={}",
                                flat.getOrderId(),
                                flat.getProductId(),
                                flat.getQuantity()
                        );

                        return flat;
                    })
                    .toList();
        });
    }

    private void publish(KStream<String, FlatOrderProduct> stream) {
        stream
                .peek((k, v) ->
                        log.info(
                                "Publishing flat order-product | orderId={} productId={}",
                                v.getOrderId(),
                                v.getProductId()
                        )
                )
                .to(
                        KafkaTopics.ORDER_PLACED_FLAT_EVENTS,
                        Produced.with(Serdes.String(), flatSerde())
                );
    }

    /* ========================
       SerDes
       ======================== */

    private JsonSerde<OrderPlacedEvent> orderSerde() {
        return new JsonSerde<>(OrderPlacedEvent.class);
    }

    private JsonSerde<FlatOrderProduct> flatSerde() {
        return new JsonSerde<>(FlatOrderProduct.class);
    }
}
