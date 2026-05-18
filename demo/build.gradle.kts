plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jooq.jooq-codegen-gradle") version "3.20.5"
}

group = "com.write"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2025.1.1"

dependencies {
	implementation("org.mapstruct:mapstruct:1.6.3")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-jooq")

	implementation("org.jooq:jooq:3.20.5")
	implementation("org.jooq:jooq-meta:3.20.5")
	implementation("org.jooq:jooq-codegen:3.20.5")

	jooqCodegen("org.jooq:jooq-codegen:3.20.5")

	implementation("org.springframework.boot:spring-boot-starter-kafka")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.flywaydb:flyway-mysql")
	implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("com.fasterxml.jackson.module:jackson-module-afterburner")
	implementation("com.nimbusds:nimbus-jose-jwt:10.9")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-amqp-test")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-starter-jooq-test")
	testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-kafka")
	testImplementation("org.testcontainers:testcontainers-mysql")
	testImplementation("org.testcontainers:testcontainers-rabbitmq")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")

	testImplementation("com.redis:testcontainers-redis:2.2.4")
	testImplementation("org.assertj:assertj-core:3.27.3")
	testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

	runtimeOnly("com.mysql:mysql-connector-j")

	jooqCodegen("com.mysql:mysql-connector-j")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
		mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.6")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jooq {
	configuration {
		jdbc {
			driver = "com.mysql.cj.jdbc.Driver"
			url = "jdbc:mysql://localhost:4000/url_shortener"
			user = "root"
			password = ""
		}

		generator {
			database {
				name = "org.jooq.meta.mysql.MySQLDatabase"
				inputSchema = "url_shortener"
				includes = ".*"
			}

			target {
				packageName = "com.write.api.generated.jooq"
				directory = "build/generated/sources/jooq/main"
			}
		}
	}
}

//sourceSets {
//	main {
//		java {
//			srcDirs("src/main/java", "build/generated-src/jooq")
//		}
//	}
//}

sourceSets {
	main {
		java {
			srcDirs("src/main/java", "src/generated/jooq") // Mudamos para src/generated/jooq para sumir do build/ clean
		}
	}
}

tasks.named("compileJava") {
	dependsOn("jooqCodegen")
}