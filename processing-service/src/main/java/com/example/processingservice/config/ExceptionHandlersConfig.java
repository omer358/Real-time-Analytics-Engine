package com.example.processingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

@Configuration
public class ExceptionHandlersConfig {

    @Bean
    public StreamsBuilderFactoryBeanConfigurer failureLogger() {
        return fb -> fb.setKafkaStreamsCustomizer(streams ->
                streams.setUncaughtExceptionHandler((t, e) -> e.printStackTrace())
        );
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer stateLogger() {
        return fb -> fb.setStateListener((newState, oldState) ->
                System.out.println("State changed: " + oldState + " -> " + newState)
        );
    }
}

