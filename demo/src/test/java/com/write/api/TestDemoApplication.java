package com.write.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.MySQLContainer;

public class TestDemoApplication {

	static void main(String[] args) {
		SpringApplication.from(DemoApplication::main)
				.with(TestcontainersConfiguration.class).run(args);
	}

	@Test
	void dockerWorks() {
		var container = new MySQLContainer<>("mysql:8.4");
		container.start();

		System.out.println(container.getJdbcUrl());

		container.stop();
	}

}
