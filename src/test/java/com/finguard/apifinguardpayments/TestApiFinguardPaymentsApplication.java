package com.finguard.apifinguardpayments;

import org.springframework.boot.SpringApplication;

public class TestApiFinguardPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.from(ApiFinguardPaymentsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
