package com.write.api;

import org.springframework.boot.SpringApplication;

public class TestDemoApplication {

	static void main(String[] args) {
		SpringApplication.from(DemoApplication::main);
//				.with(TestcontainersConfiguration.class).run(args);
	}

}
