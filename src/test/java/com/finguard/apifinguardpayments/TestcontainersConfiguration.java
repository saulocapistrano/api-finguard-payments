package com.finguard.apifinguardpayments;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
		kafka.start();
		System.setProperty("KAFKA_BOOTSTRAP_SERVERS", kafka.getBootstrapServers());
		return kafka;
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
				.withDatabaseName("payments_db")
				.withUsername("saulo")
				.withPassword("123456");

		postgres.start();
		System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
		System.setProperty("spring.datasource.username", postgres.getUsername());
		System.setProperty("spring.datasource.password", postgres.getPassword());

		return postgres;
	}

	@Bean
	@ServiceConnection(name = "redis")
	GenericContainer<?> redisContainer() {
		GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
				.withExposedPorts(6379);
		redis.start();
		return redis;
	}
}