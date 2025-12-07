package com.example.processingservice.stream;

import com.example.commonlib.events.FlatOrderProduct;
import com.example.commonlib.events.OrderPlacedEvent;
import com.example.commonlib.topics.KafkaTopics;
import com.example.processingservice.validation.OrderPlacedEventValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Configuration
@EnableKafka
@EnableKafkaStreams
@Slf4j
public class OrderPlacedStreamProcessor {
    final private OrderPlacedEventValidator validator;

    public OrderPlacedStreamProcessor(OrderPlacedEventValidator validator) {
        this.validator = validator;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "processing-service");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");

        // Default Serdes
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        props.put("spring.json.trusted.packages", "*");


        // Optional
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000);

        // Read from beginning if no committed offsets
        props.put("auto.offset.reset", "earliest");
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> fb.setStateListener((newState, oldState) -> {
            System.out.println("State transition from " + oldState + " to " + newState);
        });
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer failureLogger() {
        return fb -> fb.setKafkaStreamsCustomizer(kafkaStreams -> {
            kafkaStreams.setUncaughtExceptionHandler((thread, ex) -> {
                ex.printStackTrace();
            });
        });
    }

    /** Stream processing: flatten orders into FlatOrderProduct events */
    @Bean
    public KStream<String, FlatOrderProduct> processOrders(StreamsBuilder builder) {

        final JsonSerde<OrderPlacedEvent> orderSerde = new JsonSerde<>(OrderPlacedEvent.class);
        final JsonSerde<FlatOrderProduct> flatSerde = new JsonSerde<>(FlatOrderProduct.class);


        // Consume the orders topic
        KStream<String, OrderPlacedEvent> source =
                builder.stream(KafkaTopics.ORDER_PLACED_EVENTS, Consumed.with(Serdes.String(), orderSerde));


        KStream<String, FlatOrderProduct> flatStream = source
                .filter((key, order) -> {
                    log.info("event: {}", order);
                    boolean valid = validator.validate(order);
                    if (!valid) log.warn("Invalid event: {}", order);
                    return valid;
                })
                .flatMap((key, order) ->
                        order.getProducts().stream()
                                .map(p -> KeyValue.pair(
                                        order.getOrderId() + "-" + p.getProductId(), // unique key
                                        new FlatOrderProduct(
                                                order.getOrderId(),
                                                order.getCustomerId(),
                                                order.getTotalAmount(),
                                                order.getTimestamp().toEpochMilli(),
                                                p.getProductId(),
                                                p.getQuantity(),
                                                p.getPrice(),
                                                key
                                        )
                                ))
                                .collect(Collectors.toList())
                );

        // Produce to flattened topic
        flatStream.to(KafkaTopics.ORDER_PLACED_FLAT_EVENTS, Produced.with(Serdes.String(), flatSerde));

        // Optional logging
        flatStream.peek((k, v) -> log.info("Flattened event: {}", v));

        return flatStream;
    }

}
