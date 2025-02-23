package com.finguard.apifinguardpayments.kafka.consumer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final MeterRegistry meterRegistry;

    public KafkaConsumer(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Kafka listener that consumes messages from the "payment-events" topic.
     * Tracks message processing time and logs structured data.
     *
     * @param record Kafka ConsumerRecord containing the message.
     * @param ack Acknowledgment for manual offset management.
     */
    @KafkaListener(topics = "payment-events", groupId = "payment-group")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        long startTime = System.nanoTime();

        try {
            logger.info("Received Kafka message: key={}, value={}, partition={}, offset={}",
                    record.key(), record.value(), record.partition(), record.offset());

            // Process the message (Example: update payment status in Redis)
            processMessage(record.value());

            // Acknowledge message consumption
            ack.acknowledge();

            // Metrics: Successful message consumption
            meterRegistry.counter("kafka.consumer.messages",
                            Collections.singletonList(Tag.of("topic", record.topic())))
                    .increment();

        } catch (Exception ex) {
            logger.error("Error processing Kafka message: key={}, value={}, error={}",
                    record.key(), record.value(), ex.getMessage(), ex);

            // Metrics: Failed message consumption
            meterRegistry.counter("kafka.consumer.errors",
                            Collections.singletonList(Tag.of("topic", record.topic())))
                    .increment();
        } finally {
            long duration = System.nanoTime() - startTime;
            meterRegistry.timer("kafka.consumer.processing.time",
                            Collections.singletonList(Tag.of("topic", record.topic())))
                    .record(duration, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Processes the Kafka message (Business logic should be implemented here).
     *
     * @param message Kafka message content.
     */
    private void processMessage(String message) {
        // TODO: Implement message processing logic (e.g., update Redis, call services, etc.)
        logger.info("Processing message: {}", message);
    }
}