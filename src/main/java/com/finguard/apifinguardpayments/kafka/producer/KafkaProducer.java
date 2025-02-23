package com.finguard.apifinguardpayments.kafka.producer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Timer kafkaSendTimer;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaSendTimer = meterRegistry.timer("kafka.send.message");
    }

    public void sendMessage(String topic, String message) {
        logger.info("Sending Kafka message: topic={}, message={}", topic, message);

        long startTime = System.nanoTime();

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message).toCompletableFuture();

        future.whenComplete((result, ex) -> {
            long duration = System.nanoTime() - startTime;
            kafkaSendTimer.record(Duration.ofNanos(duration));

            if (ex == null) {
                logger.info("Message sent successfully to topic={}, partition={}, offset={} in {}ms",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        duration / 1_000_000);
            } else {
                logger.error("Error sending message to Kafka topic={}, error={}", topic, ex.getMessage());
            }
        });
    }
}
