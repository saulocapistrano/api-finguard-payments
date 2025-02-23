package com.finguard.apifinguardpayments.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Configures the RedisTemplate with appropriate key and value serializers.
     * - Key serializer: Uses StringRedisSerializer to store keys as plain strings.
     * - Value serializer: Uses GenericJackson2JsonRedisSerializer to handle complex objects.
     * - Enables transaction support for atomic Redis operations.
     *
     * @param connectionFactory The RedisConnectionFactory to connect to Redis.
     * @return A configured RedisTemplate instance.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer: Stores keys as plain strings
        template.setKeySerializer(new StringRedisSerializer());

        // Value serializer: Stores values as JSON for better compatibility with objects
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(customObjectMapper()));

        // Hash key serializer (for hash operations)
        template.setHashKeySerializer(new StringRedisSerializer());

        // Hash value serializer (to store objects inside Redis hashes)
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(customObjectMapper()));

        // Enable transaction support
        template.setEnableTransactionSupport(true);

        return template;
    }

    /**
     * Custom ObjectMapper to properly serialize Java 8 date/time fields.
     * Ensures compatibility with JavaTimeModule and other Jackson modules.
     *
     * @return Configured ObjectMapper for JSON serialization.
     */
    private ObjectMapper customObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule()) // Support for LocalDate, LocalDateTime, etc.
                .registerModule(new ParameterNamesModule()); // Handles constructor-based deserialization
    }
}