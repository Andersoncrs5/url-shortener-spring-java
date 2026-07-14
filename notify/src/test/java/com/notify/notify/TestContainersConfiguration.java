package com.notify.notify;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		KafkaContainer container = new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));

		container.setPortBindings(java.util.List.of("9092:9092"));

		return container;
	}

	@Bean
	@ServiceConnection
	MySQLContainer mysqlContainer() {
		return new MySQLContainer(DockerImageName.parse("mysql:latest")).withDatabaseName("notify_db_test");
	}

	@Bean
	@ServiceConnection(name = "redis")
	GenericContainer<?> redisContainer() {
		return new GenericContainer<>(DockerImageName.parse("redis:latest")).withExposedPorts(6379);
	}

	@Bean
	GenericContainer<?> mailpitContainer(DynamicPropertyRegistry registry) {
		GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("axllent/mailpit:latest"))
				.withExposedPorts(1025, 8025);

		registry.add("spring.mail.host", container::getHost);
		registry.add("spring.mail.port", () -> container.getMappedPort(1025));

		return container;
	}

}
