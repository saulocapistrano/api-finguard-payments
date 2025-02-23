package com.finguard.apifinguardpayments.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.kafka.core.*;

import java.util.Map;

@TestConfiguration
public class TestKafkaConfig {
//
//    @Bean
//    public EmbeddedKafkaBroker embeddedKafkaBroker() {
//        return new EmbeddedKafkaBroker(1)
//                .kafkaPorts(9092);
//    }

    @Bean
    public ProducerFactory<String, String> testProducerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> configs = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, String> testKafkaTemplate(ProducerFactory<String, String> testProducerFactory) {
        return new KafkaTemplate<>(testProducerFactory);
    }

    @Bean
    public ConsumerFactory<String, String> testConsumerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> configs = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        return new DefaultKafkaConsumerFactory<>(configs);
    }
}