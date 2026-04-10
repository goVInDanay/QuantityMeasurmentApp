package com.quantity.measurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class QuantityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantityServiceApplication.class, args);
	}

}
