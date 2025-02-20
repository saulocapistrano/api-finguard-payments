package com.finguard.apifinguardpayments;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ApiFinguardPaymentsApplicationTests {

	@Test
	void contextLoads() {
		assertThat(true).isTrue();
	}

}
