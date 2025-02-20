package com.finguard.apifinguardpayments;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThatCode;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TestApiFinguardPaymentsApplicationTest {

	@Test
	void applicationStartsSuccessfully() {
		String[] args = {};
		assertThatCode(() ->
				SpringApplication.from(ApiFinguardPaymentsApplication::main)
						.with(TestcontainersConfiguration.class)
						.run(args)
		).doesNotThrowAnyException();
	}
}
