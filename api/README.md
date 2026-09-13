# URL Shortener — Read API

A read-optimized API responsible for serving URL shortener data in a distributed, event-driven architecture.

The application is designed around **CQRS**, where the read side is completely separated from the write side. Data changes are propagated asynchronously through **Change Data Capture (CDC)** and **Apache Kafka**, allowing the Read API to maintain its own persistence and caching layers without directly depending on the transactional write database.

The project was designed with a strong focus on **scalability, asynchronous processing, resilience, observability, testability, and clean separation of responsibilities**.

---

## Architecture

The Read API is part of a distributed URL shortener architecture:

```text
                         ┌──────────────────┐
                         │     Write API    │
                         │                  │
                         │ Spring Boot      │
                         │ Transactional DB │
                         └────────┬─────────┘
                                  │
                                  ▼
                              TiCDC / CDC
                                  │
                                  ▼
                         ┌──────────────────┐
                         │      Kafka       │
                         │                  │
                         │ CDC Events       │
                         └────────┬─────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
                    ▼                           ▼
          ┌──────────────────┐        ┌──────────────────┐
          │    Read API      │        │   Other Services │
          │                  │        │                  │
          │ Spring Boot      │        │                  │
          │ Kafka Consumers  │        │                  │
          └────────┬─────────┘        └──────────────────┘
                   │
          ┌────────┴─────────┐
          │                  │
          ▼                  ▼
    ┌───────────┐      ┌───────────┐
    │ MongoDB   │      │   Redis   │
    │ Read Model│      │   Cache   │
    └───────────┘      └───────────┘
```

The Read API does not need to query the transactional database for normal read operations.

Instead, it consumes domain changes from Kafka and builds its own read model.

---

# Key Concepts

## CQRS

The application follows the **Command Query Responsibility Segregation** pattern.

The write and read workloads are separated:

```text
                  ┌──────────────────────┐
                  │      Write Side      │
                  │                      │
                  │ Commands             │
                  │ Transactions         │
                  │ Source of Truth       │
                  └──────────┬───────────┘
                             │
                             │ CDC
                             ▼
                         Kafka Topics
                             │
                             ▼
                  ┌──────────────────────┐
                  │       Read Side      │
                  │                      │
                  │ Queries              │
                  │ MongoDB              │
                  │ Redis                │
                  └──────────────────────┘
```

This allows each side of the system to evolve independently according to its workload.

The Read API is optimized for:

* high read throughput
* low-latency queries
* independent horizontal scaling
* denormalized read models
* caching
* asynchronous synchronization

---

# Event-Driven Synchronization

The Read API receives changes through Kafka consumers.

The CDC layer transforms database changes into domain-specific events such as:

* User events
* URL events
* Role events
* URL access rule events
* URL redirect rule events
* URL tag events

The corresponding CDC domain classes are located under:

```text
domain/cdc/
```

Examples:

```text
BaseCdcEvent.java
TiCdcEvent.java

RoleCdcEvent.java
UrlCdcEvent.java
UrlAccessRuleCdcEvent.java
UrlRedirectRuleCdcEvent.java
UrlTagCdcEvent.java
UserCdcEvent.java
```

The infrastructure layer then consumes these events through Kafka consumers.

```text
TiCDC
  │
  ▼
Kafka
  │
  ├── Role Events
  ├── URL Events
  ├── User Events
  ├── Access Rule Events
  ├── Redirect Rule Events
  └── Tag Events
       │
       ▼
Kafka Consumers
       │
       ▼
Application Use Cases
       │
       ▼
MongoDB Read Model
```

---

# Technology Stack

| Technology        | Purpose                                        |
| ----------------- | ---------------------------------------------- |
| Java              | Primary programming language                   |
| Spring Boot       | Application framework                          |
| Apache Kafka      | Event streaming and asynchronous communication |
| TiCDC             | Change Data Capture                            |
| MongoDB           | Read-model persistence                         |
| Redis             | Caching                                        |
| JWT               | Authentication                                 |
| Spring Security   | Authorization and security                     |
| Docker            | Containerization                               |
| Testcontainers    | Integration testing                            |
| Swagger / OpenAPI | API documentation                              |
| MapStruct         | Object mapping                                 |
| JUnit             | Unit and integration testing                   |
| Maven             | Dependency management and build                |
| Logback           | Application logging                            |

---

# Project Structure

The application follows a layered architecture with clear separation between:

* API
* Application
* Domain
* Infrastructure
* Cross-cutting utilities

```text
src
├── main
│   ├── java
│   │   └── com
│   │       └── read
│   │           └── api
│   │
│   │               ├── api
│   │               ├── application
│   │               ├── domain
│   │               ├── infrastructure
│   │               └── utils
│   │
│   └── resources
│
└── test
    ├── java
    └── resources
```

---

# API Layer

The API layer is responsible for exposing HTTP endpoints and translating HTTP requests into application-level operations.

```text
api/
├── controller/
├── dto/
└── exception/
```

## Controllers

Controllers are organized by domain resource:

```text
controller/
├── deadLetterEvent/
├── url/
├── urlAccessRule/
├── urlRedirectRule/
├── urlTag/
└── user/
```

Each resource contains dedicated components for:

* HTTP endpoints
* OpenAPI documentation
* request mapping
* pagination
* ordering
* controller-specific mapping

For example:

```text
url/
├── UrlController.java
├── UrlControllerDocs.java
├── UrlMapperController.java
├── UrlOrderBy.java
└── UrlPageRequestDTO.java
```

This keeps API concerns isolated from business logic.

---

# DTO Layer

The API DTOs are separated from the domain models.

```text
dto/
├── base/
├── deadLetterEvent/
├── metric/
├── outbox/
├── role/
├── tag/
├── url/
├── urlAccessRule/
├── urlRedirectRule/
└── user/
```

Each resource provides its own request/response and filtering structures.

For example:

```text
url/
├── AccessContextDTO.java
├── UrlDTO.java
├── UrlFilter.java
└── UrlMetricDTO.java
```

This prevents HTTP-specific representations from leaking into the domain layer.

---

# Application Layer

The application layer contains the use cases that orchestrate application behavior.

```text
application/
└── usecase/
    ├── base/
    ├── impl/
    ├── interfaces/
    └── mapper/
```

The use-case implementation is separated from its interface.

```text
interfaces/
```

contains application contracts.

```text
impl/
```

contains concrete implementations.

This allows application logic to remain independent from infrastructure concerns.

---

# Use Cases

The application contains dedicated use cases for operations such as:

```text
Find
Insert
Save
Delete
Exists
Retry
```

For example, URL operations include:

```text
FindAllUrlUseCaseImpl
FindUrlByIdUseCaseImpl
FindUrlByShortCodeUseCaseImpl
InsertUrlUseCaseImpl
SaveUrlUseCaseImpl
DeleteUrlByIdUseCaseImpl
TryRetryUrlUseCaseImpl
```

The same approach is used for:

* Users
* Roles
* URL access rules
* URL redirect rules
* URL tags
* Dead-letter events

This keeps business operations explicit and independently testable.

---

# CDC Use Cases

CDC processing is treated as an application-level operation instead of coupling Kafka consumers directly to persistence.

```text
application/usecase/impl/cdc/
```

contains dedicated CDC services for each aggregate.

```text
cdc/
├── role/
├── url/
├── urlAccessRule/
├── urlRedirectRule/
├── urlTag/
└── user/
```

The flow is therefore:

```text
Kafka Consumer
      │
      ▼
CDC Application Use Case
      │
      ▼
CDC Mapper
      │
      ▼
Repository
      │
      ▼
MongoDB
```

This separation makes the event-processing pipeline easier to test and evolve.

---

# Domain Layer

The domain layer contains the application's core models, repositories, events, enums, exceptions and domain utilities.

```text
domain/
├── cdc/
├── dto/
├── enums/
├── exceptions/
├── model/
├── repository/
├── service/
└── utils/
```

The domain layer does not depend directly on Kafka, MongoDB configuration, HTTP controllers or other infrastructure implementations.

---

# Domain Models

The domain models represent the main business concepts:

```text
model/
├── ApiKeyModel.java
├── DeadLetterEventModel.java
├── OutboxEventModel.java
├── RoleModel.java
├── UrlAccessRuleModel.java
├── UrlModel.java
├── UrlRedirectRuleModel.java
├── UrlTagModel.java
└── UserModel.java
```

Metrics are represented separately:

```text
model/
└── metrics/
    └── UrlMetricModel.java
```

This separation allows the domain representation to remain independent of persistence-specific entities.

---

# Repository Abstraction

Repository interfaces are defined in the domain layer:

```text
domain/repository/
```

Examples include:

```text
UrlRepository
UserRepository
RoleRepository
UrlTagRepository
UrlAccessRuleRepository
UrlRedirectRuleRepository
DeadLetterEventRepository
OutboxEventRepository
```

The interfaces describe what the application needs without specifying how persistence is implemented.

---

# Infrastructure Layer

Infrastructure contains concrete implementations of external integrations.

```text
infrastructure/
├── cache/
├── config/
├── job/
├── kafka/
├── mapper/
├── metrics/
├── mongo/
├── persistence/
├── properties/
└── tx/
```

This is where the application integrates with technologies such as:

* Kafka
* MongoDB
* Redis
* Spring Security
* scheduling
* metrics
* serialization
* transactions

---

# MongoDB Persistence

MongoDB-specific persistence implementations are located under:

```text
infrastructure/persistence/
```

The persistence layer separates:

```text
Domain Model
      │
      ▼
Repository Interface
      │
      ▼
Repository Implementation
      │
      ▼
Mongo Entity
      │
      ▼
MongoDB
```

Entities are kept separate from domain models:

```text
persistence/entity/
```

Examples:

```text
UrlEntity.java
UserEntity.java
RoleEntity.java
UrlTagEntity.java
UrlAccessRuleEntity.java
UrlRedirectRuleEntity.java
DeadLetterEventEntity.java
OutboxEventEntity.java
```

This prevents persistence concerns from leaking into the domain model.

---

# MongoDB Repositories

Mongo-specific repository implementations are grouped under:

```text
persistence/mongo/
```

Examples:

```text
MongoUrlRepository.java
MongoUserRepository.java
MongoRoleRepository.java
MongoUrlTagRepository.java
MongoUrlAccessRuleRepository.java
MongoUrlRedirectRuleRepository.java
MongoDeadLetterEventRepository.java
MongoOutboxEventRepository.java
```

The repository abstraction therefore remains independent of the MongoDB implementation.

---

# Redis Cache

Redis support is isolated under:

```text
infrastructure/cache/
```

The architecture exposes a domain-level cache service:

```text
domain/service/
└── RedisCrudService.java
```

while the concrete implementation lives in infrastructure:

```text
infrastructure/cache/
├── CacheConfig.java
└── RedisCrudServiceImpl.java
```

This follows the dependency inversion principle:

```text
Application / Domain
        │
        ▼
RedisCrudService
        ▲
        │
RedisCrudServiceImpl
        │
        ▼
      Redis
```

---

# Kafka Integration

Kafka infrastructure is organized under:

```text
infrastructure/kafka/
```

The implementation contains:

```text
kafka/
├── base/
├── classes/
├── consumer/
├── dlq/
└── producer/
```

Consumers are separated by aggregate:

```text
consumer/
├── role/
├── url/
├── urlAccessRule/
├── urlRedirectRule/
├── urlTag/
└── user/
```

Each aggregate has its own CDC consumer and DLQ consumer.

Example:

```text
url/
├── UrlCdcConsumer.java
└── UrlCdcConsumerDlq.java
```

This provides isolation between different event-processing pipelines.

---

# Dead Letter Queue

Failed event processing is handled through a dedicated dead-letter mechanism.

```text
kafka/
├── dlq/
│   ├── DeadLetterEvent.java
│   ├── DeadLetterPublisher.java
│   └── DeadLetterPublisherImpl.java
│
└── producer/
    └── DlqProducer.java
```

The application also maintains a persistent representation of dead-letter events:

```text
DeadLetterEventModel
DeadLetterEventEntity
DeadLetterEventRepository
```

This allows failed messages to be:

* persisted
* inspected
* retried
* deleted
* queried through the API

---

# Retry Processing

Retry operations are represented as explicit application use cases:

```text
TryRetryUrlUseCase
TryRetryUserUseCase
TryRetryRoleUseCase
TryRetryUrlTagUseCase
TryRetryUrlAccessRuleUseCase
TryRetryUrlRedirectRuleUseCase
RetryDeadLetterEventUseCase
```

A scheduled retry mechanism is also provided:

```text
infrastructure/job/
├── DeadLetterRetryJob.java
└── SchedulerConfig.java
```

This creates a controlled recovery mechanism for transient failures.

---

# Security

Security configuration is isolated under:

```text
infrastructure/config/security/
```

The implementation contains:

```text
SecurityConfig.java
SecurityFilter.java
TokenService.java
CustomUserDetailsService.java
CustomAccessDeniedHandler.java
CustomAuthenticationEntryPoint.java
```

JWT authentication is used to protect API resources.

JWT-specific configuration is centralized through:

```text
JwtProperties.java
```

The API also contains reusable authorization annotations:

```text
JwtProtected.java
```

This keeps authentication and authorization concerns separated from application use cases.

---

# Idempotency

The application provides an idempotency mechanism through a reusable annotation and aspect:

```text
utils/
└── annotation/
    └── idempotent/
        ├── IdempotencyAspect.java
        └── Idempotent.java
```

This allows idempotency behavior to be applied declaratively without duplicating implementation logic across individual use cases.

---

# Rate Limiting

Rate limiting is implemented as a cross-cutting concern:

```text
utils/
└── annotation/
    └── ratelimit/
        ├── RateLimitAspect.java
        └── RateLimited.java
```

Endpoints or application operations can therefore opt into rate limiting using the corresponding annotation.

---

# Observability

The application contains dedicated metrics infrastructure:

```text
infrastructure/metrics/
└── MetricsConfig.java
```

and a reusable observed-metric aspect:

```text
utils/
└── metrics/
    └── observed/
        ├── ObservedMetric.java
        └── ObservedMetricAspect.java
```

This allows application operations to be instrumented without embedding metrics code directly into business logic.

---

# Transaction Handling

Transaction behavior is encapsulated through an aspect-based abstraction:

```text
infrastructure/tx/
├── ResultTransaction.java
└── ResultTransactionAspect.java
```

This keeps transaction management separate from the application logic while allowing use cases to explicitly declare transactional behavior.

---

# Pagination and Filtering

The API provides reusable pagination and filtering abstractions.

Base filtering functionality:

```text
api/dto/base/
└── BaseFilter.java
```

Resource-specific filters include:

```text
UrlFilter
UserFilter
RoleFilter
UrlTagFilter
UrlAccessRuleFilter
UrlRedirectRuleFilter
DeadLetterEventFilter
```

Pagination and ordering are also modeled explicitly through classes such as:

```text
UrlPageRequestDTO
UrlOrderBy
UserPageRequestDTO
UserOrderBy
```

This provides a consistent query interface across resources.

---

# ID Generation

The project contains a Snowflake-based ID generator:

```text
domain/utils/
└── SnowflakeIdGenerator.java
```

and persistence-specific support:

```text
infrastructure/persistence/shared/id/
└── SnowflakeId.java
```

The architecture therefore supports distributed ID generation without relying exclusively on database-generated identifiers.

---

# Base62 Encoding

The domain utilities contain a Base62 implementation:

```text
domain/utils/
└── Base62.java
```

This is particularly useful for generating compact URL short codes.

The resulting concept can be represented as:

```text
Numeric ID
    │
    ▼
 Base62
    │
    ▼
Short Code
```

---

# Result-Based Error Handling

The application provides a reusable `Result` abstraction:

```text
utils/
└── result/
    └── Result.java
```

This allows application operations to represent successful and failed outcomes without coupling business logic directly to HTTP responses.

HTTP-specific error handling is centralized in:

```text
api/exception/
└── GlobalExceptionHandler.java
```

This creates a clean separation between application failures and their HTTP representation.

---

# Validation

Custom validation is provided under:

```text
utils/validation/
```

For example:

```text
isId/
├── IsId.java
└── IsIdValidator.java
```

Validation therefore remains reusable and independent from individual controllers.

---

# Testing

Testing is treated as a first-class part of the project.

The test structure mirrors the production architecture:

```text
src/test/java/com/read/api/
├── api/
├── application/
├── cdc/
├── repository/
└── ...
```

---

## API Tests

Controller integration tests cover the main HTTP resources:

```text
api/controller/
├── DeadLetterEventControllerTest.java
├── UrlControllerTest.java
├── UrlAccessRuleControllerTest.java
├── UrlRedirectRuleControllerTest.java
├── UrlTagControllerTest.java
└── UserControllerTest.java
```

---

## Application Tests

Use cases are tested individually.

For example:

```text
application/usecase/services/url/
├── DeleteUrlByIdUseCaseImplTest.java
├── FindAllUrlUseCaseImplTest.java
├── FindUrlByIdUseCaseImplTest.java
├── FindUrlByShortCodeUseCaseImplTest.java
├── InsertUrlUseCaseImplTest.java
└── SaveUrlUseCaseImplTest.java
```

The same testing strategy is applied to users, roles, tags, access rules, redirect rules and dead-letter events.

---

## CDC Tests

CDC processing has dedicated tests:

```text
cdc/
├── base/
├── role/
└── user/
```

and application-level CDC tests:

```text
application/usecase/cdc/
```

This allows event-processing behavior to be validated independently from the HTTP layer.

---

## Repository Tests

Persistence implementations have their own tests:

```text
repository/
├── DeadLetterEventRepositoryImplTest.java
├── OutboxEventRepositoryImplTest.java
├── RoleRepositoryImplTest.java
├── UrlAccessRuleRepositoryImplTest.java
├── UrlRedirectRuleRepositoryImplTest.java
├── UrlRepositoryImplTest.java
├── UrlTagRepositoryImplTest.java
└── UserRepositoryImplTest.java
```

---

# Testcontainers

Integration tests use Testcontainers infrastructure:

```text
TestcontainersConfiguration.java
```

This allows tests to execute against real infrastructure components instead of relying exclusively on mocks.

The test environment is configured through:

```text
src/test/resources/application-test.yaml
```

---

# API Documentation

The project provides OpenAPI/Swagger documentation.

Swagger-specific configuration is located at:

```text
infrastructure/config/swagger/
└── SwaggerConfig.java
```

Controller documentation is separated from controller implementation:

```text
UrlController.java
UrlControllerDocs.java
```

This keeps API documentation concerns separate from endpoint implementation.

---

# Configuration

Application configuration is centralized in:

```text
src/main/resources/application.yaml
```

Infrastructure-specific configuration is further organized through dedicated property classes:

```text
infrastructure/properties/
├── CorsProperties.java
├── JwtProperties.java
└── KafkaProperties.java
```

This provides typed configuration instead of scattering configuration access throughout the application.

---

# Logging

Application logging is configured through:

```text
src/main/resources/logback-spring.xml
```

Application logs can be stored under:

```text
logs/
└── api-spring.log
```

---

# Docker

The project contains a dedicated Dockerfile:

```text
Dockerfile
```

The application can therefore be packaged as a container and executed independently from the local development environment.

---

# Native Image Support

The project contains native-image configuration:

```text
src/main/resources/META-INF/native-image/
└── reflect-config.json
```

This configuration is used to provide reflection metadata required by native-image environments.

---

# Complete Package Structure

The high-level production structure can be summarized as:

```text
com.read.api
│
├── api
│   ├── controller
│   │   ├── deadLetterEvent
│   │   ├── url
│   │   ├── urlAccessRule
│   │   ├── urlRedirectRule
│   │   ├── urlTag
│   │   └── user
│   │
│   ├── dto
│   │   ├── base
│   │   ├── deadLetterEvent
│   │   ├── metric
│   │   ├── outbox
│   │   ├── role
│   │   ├── tag
│   │   ├── url
│   │   ├── urlAccessRule
│   │   ├── urlRedirectRule
│   │   └── user
│   │
│   └── exception
│
├── application
│   └── usecase
│       ├── base
│       ├── impl
│       │   ├── cdc
│       │   ├── deadLetterEvent
│       │   ├── role
│       │   ├── url
│       │   ├── urlAccessRule
│       │   ├── urlRedirectRule
│       │   ├── urlTag
│       │   ├── urlTagLink
│       │   ├── user
│       │   └── userRole
│       │
│       ├── interfaces
│       └── mapper
│
├── domain
│   ├── cdc
│   ├── dto
│   ├── enums
│   ├── exceptions
│   ├── model
│   ├── repository
│   ├── service
│   └── utils
│
├── infrastructure
│   ├── cache
│   ├── config
│   │   ├── jackson
│   │   ├── kafka
│   │   ├── security
│   │   └── swagger
│   ├── job
│   ├── kafka
│   │   ├── base
│   │   ├── classes
│   │   ├── consumer
│   │   ├── dlq
│   │   └── producer
│   ├── mapper
│   ├── metrics
│   ├── mongo
│   ├── persistence
│   ├── properties
│   └── tx
│
└── utils
    ├── annotation
    │   ├── idempotent
    │   └── ratelimit
    ├── metrics
    ├── page
    ├── result
    └── validation
```

---

# Design Principles

The project is structured around several software engineering principles:

### Separation of Concerns

HTTP, application logic, domain logic and infrastructure are isolated.

### Dependency Inversion

The application depends on abstractions such as repositories and services rather than concrete infrastructure implementations.

### Single Responsibility

Controllers, use cases, repositories, consumers and infrastructure components have clearly defined responsibilities.

### Explicit Use Cases

Business operations are represented by dedicated use-case interfaces and implementations.

### Event-Driven Architecture

Read models are updated asynchronously through CDC events and Kafka.

### Resilience

Failed events can be routed to a dead-letter flow and retried through dedicated mechanisms.

### Testability

The architecture allows application, repository, CDC and API components to be tested independently.

---

# Running the Project

## Requirements

Recommended environment:

* Java
* Maven
* Docker
* MongoDB
* Redis
* Apache Kafka
* TiCDC / CDC event source

---

## Build

Using the Maven wrapper:

```bash
./mvnw clean package
```

On Windows:

```powershell
mvnw.cmd clean package
```

---

## Run

```bash
./mvnw spring-boot:run
```

---

## Run Tests

```bash
./mvnw test
```

Integration tests require the infrastructure supported by the Testcontainers configuration.

---

# Application Responsibilities

The Read API is responsible for:

* serving URL read operations
* maintaining the read model
* consuming CDC events
* synchronizing MongoDB with upstream changes
* providing Redis-based caching
* processing asynchronous events
* handling failed events
* retrying dead-letter events
* exposing URL metrics
* providing authentication and authorization
* enforcing rate limits
* supporting idempotent operations
* exposing paginated and filtered APIs
* providing API documentation
* exposing observability hooks

---

# Why a Dedicated Read API?

Separating the read model from the transactional write model provides several architectural advantages.

### Independent Scaling

Read workloads can be scaled independently from write workloads.

### Read Optimization

MongoDB can be modeled specifically around query patterns rather than transactional requirements.

### Reduced Coupling

The Read API does not need to access the write database directly for normal queries.

### Asynchronous Propagation

Changes are propagated through CDC and Kafka rather than synchronous service-to-service communication.

### Specialized Caching

Redis can be optimized specifically for frequently accessed data.

### Fault Isolation

Failures in read-side processing do not necessarily affect the transactional write system.

---

# Architectural Trade-offs

CQRS and event-driven synchronization introduce additional complexity.

The system must handle:

* eventual consistency
* duplicate events
* failed events
* retry processing
* ordering considerations
* consumer failures
* read-model reconstruction
* cache invalidation
* operational complexity

The project addresses these concerns through:

* idempotency
* dead-letter handling
* retry mechanisms
* CDC-specific consumers
* explicit application use cases
* repository abstractions
* integration testing
* observability

---

# Project Goal

The goal of this project is not only to implement a URL shortener.

It serves as a practical exploration of **distributed backend architecture**, particularly:

* CQRS
* Event-Driven Architecture
* Change Data Capture
* Kafka-based asynchronous processing
* Read-model design
* MongoDB
* Redis caching
* distributed ID generation
* resilience patterns
* idempotency
* rate limiting
* observability
* clean architecture
* integration testing

The project intentionally favors architectural separation and infrastructure-oriented design over a simple CRUD implementation.

---

# License

This project is intended primarily as a technical and educational project focused on backend engineering, distributed systems and software architecture.
