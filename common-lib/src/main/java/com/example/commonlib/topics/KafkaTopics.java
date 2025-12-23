package com.example.commonlib.topics;

public final class KafkaTopics {

    public static final String ORDER_PLACED_EVENTS = "order-placed-events";
    public static final String ORDER_PLACED_FLAT_EVENTS = "order-placed-flat-events";
    public static final String PRODUCT_DB_EVENTS = "rta.public.product";
    public static final String ORDER_PLACED_DETAILED_EVENTS =  "order-placed-details-events";
    private KafkaTopics() {} // prevent instantiation
}
