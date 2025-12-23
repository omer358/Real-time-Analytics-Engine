package com.example.processingservice.topology;

import com.example.commonlib.events.FlatOrderProduct;
import com.example.commonlib.events.OrderPlacedEvent;
import com.example.commonlib.models.Product;
import com.example.processingservice.mapper.FlatOrderProductMapper;
import com.example.processingservice.validation.OrderPlacedEventValidator;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Instant;
import java.util.Collections;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class OrderPlacedTopologyTest {

    private final JsonSerde<OrderPlacedEvent> orderSerde = new JsonSerde<>(OrderPlacedEvent.class);
    private final JsonSerde<FlatOrderProduct> flatSerde = new JsonSerde<>(FlatOrderProduct.class);
    private final Serdes.StringSerde stringSerde = new Serdes.StringSerde();
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, OrderPlacedEvent> inputTopic;
    private TestOutputTopic<String, FlatOrderProduct> outputTopic;
    @Mock
    private FlatOrderProductMapper mapper;
    @BeforeEach
    void setup() {
        // Build topology
        StreamsBuilder builder = new StreamsBuilder();
        OrderPlacedEventValidator validator = new OrderPlacedEventValidator();
        new OrderPlacedTopology(validator,mapper).orderPlacedStream(builder);

        Topology topology = builder.build();

        Properties props = new Properties();
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "test-processing-service");
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234"); // not used by TopologyTestDriver
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.setProperty(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "100");
        props.setProperty("auto.offset.reset", "earliest");

        testDriver = new TopologyTestDriver(topology, props);

        inputTopic = testDriver.createInputTopic(
                "order-placed-events",
                stringSerde.serializer(),
                orderSerde.serializer()
        );

        outputTopic = testDriver.createOutputTopic(
                "order-placed-flat-events",
                stringSerde.deserializer(),
                flatSerde.deserializer()
        );
    }

    @AfterEach
    void tearDown() {
        testDriver.close();
    }

    @Test
    void shouldProduceFlattenedEvent() {
        OrderPlacedEvent order = new OrderPlacedEvent(
                "order-1",
                "customer-1",
                100.0,
                Instant.now(),
                Collections.singletonList(new Product("product-1", 2, 50.0))
        );

        inputTopic.pipeInput(order.getOrderId(), order);

        FlatOrderProduct flat = outputTopic.readValue();
        assertThat(flat.getOrderId(), equalTo("order-1"));
        assertThat(flat.getProductId(), equalTo("product-1"));
        assertThat(flat.getQuantity(), equalTo(2));
    }

    @Test
    void shouldFilterInvalidEvent() {
        OrderPlacedEvent invalidOrder = new OrderPlacedEvent(
                "order-2",
                "customer-2",
                0.0, // invalid because totalAmount <= 0
                Instant.now(),
                Collections.singletonList(new Product("product-2", 1, 0.0))
        );

        inputTopic.pipeInput(invalidOrder.getOrderId(), invalidOrder);

        assertThat(outputTopic.isEmpty(), equalTo(true));
    }

    @Test
    void shouldFlattenMultipleProducts() {
        OrderPlacedEvent order = new OrderPlacedEvent(
                "order-3",
                "customer-3",
                200.0,
                Instant.now(),
                java.util.List.of(
                        new Product("product-A", 1, 50.0),
                        new Product("product-B", 3, 150.0)
                )
        );

        inputTopic.pipeInput(order.getOrderId(), order);

        FlatOrderProduct flat1 = outputTopic.readValue();
        FlatOrderProduct flat2 = outputTopic.readValue();

        assertThat(flat1.getOrderId(), equalTo("order-3"));
        assertThat(flat2.getOrderId(), equalTo("order-3"));
    }
}
