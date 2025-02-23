package com.finguard.apifinguardpayments.application;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final Timer cacheWriteTimer;
    private final Timer cacheReadTimer;
    private final Counter cacheMissCounter;
    private final Counter cacheHitCounter;
    private final Counter cacheDeleteCounter;

    public RedisService(RedisTemplate<String, Object> redisTemplate, MeterRegistry meterRegistry) {
        this.redisTemplate = redisTemplate;
        this.cacheWriteTimer = meterRegistry.timer("redis.cache.write");
        this.cacheReadTimer = meterRegistry.timer("redis.cache.read");
        this.cacheMissCounter = meterRegistry.counter("redis.cache.miss");
        this.cacheHitCounter = meterRegistry.counter("redis.cache.hit");
        this.cacheDeleteCounter = meterRegistry.counter("redis.cache.delete");
    }

    /**
     * Stores a key-value pair in Redis. If the value is null, the key is removed.
     *
     * @param key   The key to store.
     * @param value The value to store, or null to delete the key.
     */
    public void setValue(String key, Object value) {
        if (value == null) {
            deleteCachedValue(key);
        } else {
            long startTime = System.nanoTime();
            try {
                redisTemplate.opsForValue().set(key, value);
                log.info("✅ Stored value in Redis - Key: {}, Value: {}", key, value);
            } catch (Exception e) {
                log.error("❌ Failed to store value in Redis - Key: {} | Error: {}", key, e.getMessage());
                throw new RuntimeException("Redis cache operation failed", e);
            } finally {
                cacheWriteTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            }
        }
    }

    /**
     * Stores a key-value pair in Redis with an expiration time.
     *
     * @param key        The key to store.
     * @param value      The value to store.
     * @param expiration Time in seconds before the key expires.
     */
    public void setValueWithExpiration(String key, Object value, long expiration) {
        long startTime = System.nanoTime();
        try {
            redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.SECONDS);
            log.info("✅ Stored value in Redis with expiration - Key: {}, Expiration: {}s", key, expiration);
        } catch (Exception e) {
            log.error("❌ Failed to store value with expiration - Key: {} | Error: {}", key, e.getMessage());
            throw new RuntimeException("Redis cache operation failed", e);
        } finally {
            cacheWriteTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Retrieves a value from Redis.
     *
     * @param key The key to retrieve.
     * @return The stored value, or null if not found.
     */
    public Object getValue(String key) {
        long startTime = System.nanoTime();
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                cacheMissCounter.increment();
                log.warn("⚠️ Cache miss - Key: {}", key);
            } else {
                cacheHitCounter.increment();
                log.info("✅ Cache hit - Key: {}", key);
            }
            return value;
        } catch (Exception e) {
            log.error("❌ Failed to retrieve value from Redis - Key: {} | Error: {}", key, e.getMessage());
            throw new RuntimeException("Redis read operation failed", e);
        } finally {
            cacheReadTimer.record(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Deletes a key from Redis.
     *
     * @param key The key to delete.
     */
    public void deleteCachedValue(String key) {
        try {
            redisTemplate.delete(key);
            cacheDeleteCounter.increment();
            log.info("🗑️ Deleted key from Redis - Key: {}", key);
        } catch (Exception e) {
            log.error("❌ Failed to delete key from Redis - Key: {} | Error: {}", key, e.getMessage());
            throw new RuntimeException("Redis delete operation failed", e);
        }
    }
}
