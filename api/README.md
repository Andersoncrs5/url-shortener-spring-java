# URL Shortener — Read API

The **Read API** is the query side of a distributed URL Shortener system designed around **CQRS (Command Query Responsibility Segregation)**.

While the Write API is responsible for transactional operations and publishing changes, the Read API consumes **Change Data Capture (CDC)** events through Apache Kafka and continuously builds optimized read models in **MongoDB**.

The architecture is designed to separate write and read workloads, allowing each side of the system to evolve and scale independently.

---

## Architecture

The Read API follows a layered architecture inspired by **Clean Architecture and Hexagonal Architecture**, separating the API, application, domain, and infrastructure concerns.

The main data flow is:

```text
                         ┌──────────────────────┐
                         │       Write API      │
                         │    Command Side      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    Write Database    │
                         └──────────┬───────────┘
                                    │
                                    │ CDC
                                    ▼
                         ┌──────────────────────┐
                         │        TiCDC         │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │        Kafka         │
                         └──────────┬───────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │                     │                     │
              ▼                     ▼                     ▼
       ┌────────────┐        ┌────────────┐        ┌────────────┐
       │ URL Events │        │ User Events│        │ Role Events│
       └─────┬──────┘        └─────┬──────┘        └─────┬──────┘
             │                     │                     │
             └─────────────────────┼─────────────────────┘
                                   ▼
                         ┌──────────────────────┐
                         │       Read API       │
                         │     Query Side       │
                         └──────────┬───────────┘
                                    │
                         ┌──────────┴───────────┐
                         │                      │
                         ▼                      ▼
                ┌─────────────────┐    ┌─────────────────┐
                │     MongoDB     │    │      Redis      │
                │   Read Models   │    │      Cache      │
                └─────────────────┘    └─────────────────┘
```

The resulting architecture allows the system to use a persistence model optimized for queries without coupling read operations directly to the transactional write database.

---

## Key Responsibilities

The Read API is responsible for:

* Consuming CDC events from Kafka
* Processing changes asynchronously
* Maintaining MongoDB read models
* Serving optimized query operations
* Supporting pagination and filtering
* Providing URL, user, role, tag, and rule queries
* Maintaining read-side consistency with the write database
* Handling failed event processing through a Dead Letter Queue
* Retrying failed events
* Providing cache support through Redis
* Applying authentication and authorization
* Applying request rate limiting
* Supporting idempotent processing
* Exposing metrics and operational information
* Providing REST and OpenAPI documentation

---

# CQRS

The system separates commands from queries.

```text
             COMMAND SIDE                         QUERY SIDE

        ┌──────────────────┐                ┌──────────────────┐
        │     Write API    │                │     Read API     │
        └────────┬─────────┘                └────────▲─────────┘
                 │                                   │
                 ▼                                   │
        ┌──────────────────┐                         │
        │ Write Database   │                         │
        └────────┬─────────┘                         │
                 │                                   │
                 │ CDC                               │
                 ▼                                   │
        ┌──────────────────┐                         │
        │      TiCDC       │                         │
        └────────┬─────────┘                         │
                 │                                   │
                 ▼                                   │
        ┌──────────────────┐                         │
        │      Kafka       │─────────────────────────┘
        └──────────────────┘
```

The two sides have different responsibilities.

### Write API

The command side handles:

* Creating and modifying data
* Transactional operations
* Domain operations
* Persistence in the write database
* Publishing changes through CDC

### Read API

The query side handles:

* Consuming CDC events
* Updating read models
* Querying data
* Filtering and pagination
* Serving read-heavy workloads
* Caching frequently accessed data

This separation prevents read-heavy workloads from unnecessarily impacting the transactional database.

---

# Change Data Capture

One of the main characteristics of the system is its use of **Change Data Capture (CDC)**.

Instead of synchronously calling the Read API every time data changes, database changes are captured and propagated asynchronously.

```text
Database Change
      │
      ▼
    TiCDC
      │
      ▼
    Kafka
      │
      ▼
 Read API Consumer
      │
      ▼
CDC Use Case
      │
      ▼
MongoDB Read Model
```

The application contains dedicated CDC processing components for:

* Users
* Roles
* URLs
* URL access rules
* URL redirect rules
* URL tags

Each aggregate has its own CDC mapper and application-level processing logic.

---

# Kafka Consumers

Kafka consumers are organized by aggregate type.

```text
infrastructure/kafka/consumer/

├── role
│   ├── RoleCdcConsumer
│   └── RoleCdcConsumerDlq
│
├── url
│   ├── UrlCdcConsumer
│   └── UrlCdcConsumerDlq
│
├── urlAccessRule
│   ├── UrlAccessRuleCdcConsumer
│   └── UrlAccessRuleCdcConsumerDlq
│
├── urlRedirectRule
│   ├── UrlRedirectRuleCdcConsumer
│   └── UrlRedirectRuleCdcConsumerDlq
│
├── urlTag
│   ├── UrlTagCdcConsumer
│   └── UrlTagCdcConsumerDlq
│
└── user
    ├── UserCdcConsumer
    └── UserCdcConsumerDlq
```

Common consumer behavior is abstracted through reusable base components:

```text
AbstractCdcConsumer
AbstractDlqConsumer
```

This avoids duplicating infrastructure logic between individual consumers.

---

# CDC Domain Model

CDC events are represented explicitly inside the domain layer.

```text
domain/cdc/

├── BaseCdcEvent
├── TiCdcEvent
└── classes
    ├── RoleCdcEvent
    ├── UrlCdcEvent
    ├── UrlAccessRuleCdcEvent
    ├── UrlRedirectRuleCdcEvent
    ├── UrlTagCdcEvent
    └── UserCdcEvent
```

This provides a dedicated domain representation for change events instead of coupling the application directly to Kafka infrastructure.

The domain also defines CDC-specific concepts such as:

* Event types
* Aggregate types
* Topics
* CDC event types
* Outbox status
* Dead letter status

---

# MongoDB Read Models

MongoDB is used as the primary persistence layer for the query side.

The read API contains MongoDB-specific repositories for the application's main aggregates:

```text
MongoUrlRepository
MongoUserRepository
MongoRoleRepository
MongoUrlTagRepository
MongoUrlAccessRuleRepository
MongoUrlRedirectRuleRepository
MongoOutboxEventRepository
MongoDeadLetterEventRepository
```

This allows the query side to use a persistence model designed specifically for read operations.

The application separates domain repositories from infrastructure implementations:

```text
Domain Repository
       │
       ▼
Repository Implementation
       │
       ▼
MongoDB Repository
```

This keeps MongoDB-specific details inside the infrastructure layer.

---

# Redis Cache

Redis is used as a caching layer for frequently accessed data.

The cache abstraction is represented in the domain through:

```text
RedisCrudService
```

with its infrastructure implementation:

```text
RedisCrudServiceImpl
```

The separation allows application components to depend on an abstraction rather than directly depending on Redis infrastructure.

```text
Application
     │
     ▼
RedisCrudService
     │
     ▼
RedisCrudServiceImpl
     │
     ▼
   Redis
```

This can reduce database load and improve response times for frequently accessed resources.

---

# Dead Letter Queue

The system includes a dedicated **Dead Letter Queue (DLQ)** mechanism for CDC processing failures.

When an event cannot be successfully processed, it can be routed to the dead-letter flow instead of being permanently lost.

```text
Kafka
  │
  ▼
CDC Consumer
  │
  ├── Success ──────────► MongoDB
  │
  └── Failure
        │
        ▼
       DLQ
        │
        ▼
DeadLetterEvent
        │
        ▼
Persisted Failure
        │
        ▼
Retry Job
        │
        ▼
Retry Processing
```

The system contains dedicated components for:

* Publishing dead-letter events
* Consuming DLQ messages
* Persisting failed events
* Querying dead-letter events
* Retrying failed events
* Deleting processed dead-letter events

The application also provides:

```text
DeadLetterRetryJob
```

for automated retry processing.

---

# Retry Strategy

Retry behavior is implemented at multiple levels of the application.

CDC processing defines reusable retry-oriented use cases such as:

```text
AbstractRetryDeadLetterUseCase
```

and aggregate-specific retry operations such as:

```text
TryRetryUrlUseCase
TryRetryUserUseCase
TryRetryRoleUseCase
TryRetryUrlTagUseCase
TryRetryUrlAccessRuleUseCase
TryRetryUrlRedirectRuleUseCase
```

MongoDB-specific transient failures are also translated through:

```text
MongoRetryTranslator
MongoRetryTranslation
```

This allows infrastructure-level transient failures to be represented through application/domain abstractions.

---

# API

The Read API exposes REST endpoints for the main queryable resources.

## URL

Supports operations such as:

* Find URL by ID
* Find URL by short code
* List URLs
* Filter URLs
* Paginate URL results
* Delete URLs

## Users

Supports:

* Find users
* Filter users
* Find user by ID
* User pagination
* User deletion

## Roles

Supports:

* Find roles
* Filter roles
* Find role by ID
* Role pagination
* Role deletion

## URL Tags

Supports:

* Find tags
* Filter tags
* Find tag by ID
* Search by name
* Search by slug
* Tag pagination
* Tag deletion

## URL Access Rules

Supports:

* Find access rules
* Filter access rules
* Find rules by URL
* Find rule by ID
* Validate rule existence
* Delete access rules

## URL Redirect Rules

Supports:

* Find redirect rules
* Filter redirect rules
* Find rule by ID
* Delete redirect rules

## Dead Letter Events

Provides operational endpoints for:

* Listing dead-letter events
* Finding events by ID
* Persisting events
* Retrying events
* Deleting events

---

# Pagination and Filtering

The API provides reusable filtering and pagination abstractions.

Examples include:

```text
UrlPageRequestDTO
UserPageRequestDTO
RolePageRequestDTO
UrlTagPageRequestDTO
UrlAccessRulePageRequestDTO
UrlRedirectRulePageRequestDTO
DeadLetterEventPageRequestDTO
```

Each resource also defines dedicated filtering and ordering models.

This provides consistent query semantics across the API.

---

# Authentication and Security

The application contains a dedicated Spring Security configuration.

The security infrastructure includes:

```text
SecurityConfig
SecurityFilter
TokenService
CustomUserDetailsService
CustomAuthenticationEntryPoint
CustomAccessDeniedHandler
UserPrincipal
```

JWT is used for authentication, with dedicated configuration properties:

```text
JwtProperties
```

Controllers can declare JWT-protected operations using:

```text
@JwtProtected
```

This keeps security concerns separated from the application's core business logic.

---

# Rate Limiting

The application contains a reusable annotation-based rate limiting mechanism:

```text
@RateLimited
```

implemented through:

```text
RateLimitAspect
```

This provides a declarative way to apply rate limits without embedding rate-limiting logic directly inside controllers.

---

# Idempotency

The Read API also contains an idempotency mechanism based on an annotation and AOP aspect:

```text
@Idempotent
IdempotencyAspect
```

This provides a reusable cross-cutting mechanism for preventing duplicate processing where idempotent behavior is required.

---

# Observability

The application contains infrastructure for collecting execution and application metrics.

The project defines an annotation-based observation mechanism:

```text
@ObservedMetric
```

implemented through:

```text
ObservedMetricAspect
```

The architecture also contains dedicated metric DTOs and domain models for URL-related metrics.

---

# Snowflake IDs

The application contains a Snowflake-based identifier generation mechanism:

```text
SnowflakeIdGenerator
SnowflakeConfiguration
SnowflakeId
```

Snowflake-style identifiers provide distributed ID generation without requiring a centralized database sequence.

This is particularly useful in distributed architectures where multiple application instances may generate identifiers independently.

---

# Base62

The domain also contains a Base62 utility:

```text
Base62
```

Base62 encoding is useful for generating compact representations of identifiers and is particularly suitable for URL-shortening use cases.

---

# Error Handling

The REST API uses a centralized exception handling mechanism:

```text
GlobalExceptionHandler
```

The project also defines structured validation error representations:

```text
ValidationErrorItem
ValidationErrorResponse
```

and a generic result abstraction:

```text
Result
```

This helps maintain consistent error and response behavior throughout the application.

---

# Transaction Handling

The infrastructure contains an aspect-based transaction abstraction:

```text
ResultTransaction
ResultTransactionAspect
```

This allows transaction handling to be integrated with the application's result-oriented programming model.

---

# Native Image Support

The project contains configuration for native-image reflection:

```text
src/main/resources/META-INF/native-image/
└── reflect-config.json
```

This indicates that the application is prepared for environments where reflection metadata must be explicitly provided for native compilation.

Native deployment can be particularly useful for:

* Fast startup
* Reduced memory consumption
* Containerized workloads
* Serverless environments
* Highly scalable services

---

# API Documentation

The project contains dedicated Swagger/OpenAPI configuration and controller documentation classes.

Examples include:

```text
SwaggerConfig
UrlControllerDocs
UserControllerDocs
RoleControllerDocs
UrlTagControllerDocs
UrlAccessRuleControllerDocs
UrlRedirectRuleControllerDocs
DeadLetterEventControllerDocs
```

This keeps API documentation concerns separate from controller implementation.

Swagger UI customization is also included under:

```text
src/main/resources/static/swagger-ui/
```

---

# Project Structure

The project is organized into four main architectural areas:

```text
com.read.api
│
├── api
│   ├── controller
│   ├── dto
│   └── exception
│
├── application
│   └── usecase
│       ├── base
│       ├── impl
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
│   ├── job
│   ├── kafka
│   ├── mapper
│   ├── metrics
│   ├── mongo
│   ├── persistence
│   ├── properties
│   └── tx
│
└── utils
    ├── annotation
    ├── metrics
    ├── page
    ├── result
    └── validation
```

### API Layer

Responsible for HTTP concerns:

* REST controllers
* DTOs
* Request mapping
* Response mapping
* API documentation
* Exception handling

### Application Layer

Responsible for use cases and application orchestration.

```text
application/usecase
```

contains explicit interfaces and implementations for operations such as:

* Finding entities
* Inserting entities
* Updating entities
* Deleting entities
* Processing CDC events
* Retrying failed events

### Domain Layer

Contains the core concepts used by the application:

* Domain models
* CDC events
* Repository abstractions
* Enums
* Domain exceptions
* Utility components

### Infrastructure Layer

Contains external technology integrations:

* MongoDB
* Redis
* Kafka
* Spring Security
* Scheduling
* Persistence implementations
* Metrics
* Configuration

This separation prevents infrastructure concerns from leaking into the core application logic.

---

# Technology Stack

| Technology        | Purpose                                 |
| ----------------- | --------------------------------------- |
| Java              | Main programming language               |
| Spring Boot       | Application framework                   |
| Spring Security   | Authentication and authorization        |
| JWT               | Stateless authentication                |
| Apache Kafka      | Event streaming and CDC event transport |
| TiCDC             | Change Data Capture                     |
| MongoDB           | Query-side persistence                  |
| Redis             | Caching                                 |
| Maven             | Dependency and build management         |
| Testcontainers    | Integration testing                     |
| Swagger / OpenAPI | API documentation                       |
| Docker            | Containerization                        |

---

# Testing

Testing is an important part of the project.

The test suite is organized according to the application architecture.

```text
src/test/java/com/read/api

├── api
│   └── controller
│
├── application
│   └── usecase
│       ├── cdc
│       └── services
│
├── cdc
│
├── repository
│
└── TestcontainersConfiguration
```

The project contains tests for:

* REST controllers
* Application use cases
* CDC processing
* Repository implementations
* URL operations
* User operations
* Role operations
* URL tags
* URL access rules
* URL redirect rules
* Dead-letter event processing

Integration testing is supported through **Testcontainers**.

---

# Test Architecture

Reusable test infrastructure is provided through classes such as:

```text
BaseIntegrationTest
BaseUseCaseTest
BaseCdcTest
BaseRepositoryTest
TestcontainersConfiguration
```

This reduces duplicated setup code and allows different layers of the application to be tested consistently.

---

# Resilience and Failure Handling

The Read API was designed with asynchronous failure scenarios in mind.

The architecture provides mechanisms for:

* Kafka consumer failures
* Dead-letter publishing
* Dead-letter persistence
* Automated retries
* MongoDB transient error translation
* Idempotent processing
* Centralized exception handling
* Rate limiting

This is especially important in an event-driven architecture where consumers must be able to recover from temporary failures without losing events.

---

# Read Model Consistency

The Read API intentionally uses asynchronous synchronization.

The flow is:

```text
Write
  │
  ▼
Write Database
  │
  ▼
CDC
  │
  ▼
Kafka
  │
  ▼
Read API
  │
  ▼
MongoDB
```

Therefore, the read model may temporarily lag behind the write model.

This is an intentional trade-off of the CQRS architecture.

The benefit is that the query side can be optimized independently from the transactional side.

---

# Scalability

Because the Read API is separated from the command side, the query infrastructure can be scaled independently.

For example:

```text
                 Kafka
                   │
        ┌──────────┼──────────┐
        ▼          ▼          ▼
    Read API   Read API   Read API
     Instance   Instance   Instance
        │          │          │
        └──────────┼──────────┘
                   ▼
                MongoDB
                   │
                   ▼
                 Redis
```

This architecture is particularly suitable for workloads where read traffic significantly exceeds write traffic.

Kafka consumer groups can also distribute event processing across multiple application instances.

---

# Design Principles

The project follows several software engineering principles:

### Separation of Concerns

API, application, domain, and infrastructure responsibilities are kept separate.

### Dependency Inversion

The application depends on abstractions such as repository and service interfaces rather than concrete infrastructure implementations.

### Explicit Use Cases

Business operations are represented through dedicated use-case interfaces and implementations.

### Asynchronous Communication

CDC events are propagated through Kafka instead of synchronously coupling the Write API and Read API.

### Failure Isolation

Failed event processing can be isolated through DLQ mechanisms without stopping the entire event-processing pipeline.

### Read Optimization

MongoDB and Redis provide persistence and caching mechanisms optimized for query workloads.

### Testability

The architecture provides clear boundaries that allow individual components and integrations to be tested independently.

---

# CQRS + CDC vs Traditional CRUD

A traditional architecture might look like:

```text
Client
  │
  ▼
API
  │
  ▼
Single Database
```

The architecture used by this project is closer to:

```text
                    ┌──────────────┐
                    │   Write API  │
                    └──────┬───────┘
                           │
                           ▼
                     Write Database
                           │
                           ▼
                          CDC
                           │
                           ▼
                         Kafka
                           │
                           ▼
                    ┌──────────────┐
                    │   Read API   │
                    └──────┬───────┘
                           │
                           ▼
                      Read Model
                           │
                    ┌──────┴──────┐
                    ▼             ▼
                 MongoDB        Redis
```

This introduces additional infrastructure and eventual consistency, but provides greater flexibility for read-heavy and distributed workloads.

---

# Why This Architecture?

The main goal is to explore how a production-oriented distributed application can separate transactional writes from optimized queries.

The project focuses on several real-world backend engineering challenges:

* Distributed data synchronization
* Change Data Capture
* Event-driven architectures
* CQRS
* Asynchronous processing
* Read model construction
* Failure recovery
* Dead-letter queues
* Retry mechanisms
* Caching
* Horizontal scalability
* API security
* Observability
* Integration testing

Rather than treating the URL shortener as a simple CRUD application, the project uses it as a practical environment for exploring distributed-system design.

---

# Running the Application

## Requirements

Make sure the following components are available:

* Java
* Maven
* Docker
* MongoDB
* Redis
* Apache Kafka

The CDC infrastructure also requires a compatible TiCDC setup.

---

## Configuration

Application configuration is located at:

```text
src/main/resources/application.yaml
```

Test configuration is located at:

```text
src/test/resources/application-test.yaml
```

Environment-specific values should be configured through environment variables or external configuration rather than hard-coded credentials.

---

## Build

Using Maven Wrapper:

```bash
./mvnw clean package
```

On Windows:

```powershell
mvnw.cmd clean package
```

---

## Run

Using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or run the generated JAR:

```bash
java -jar target/*.jar
```

---

# Docker

The project includes a `Dockerfile` for containerized deployments.

Build the image:

```bash
docker build -t url-shortener-read-api .
```

Run the container:

```bash
docker run --rm url-shortener-read-api
```

External dependencies such as Kafka, MongoDB, and Redis should be configured according to the deployment environment.

---

# API Documentation

After starting the application, Swagger UI can be accessed through the configured application endpoint.

The API documentation is generated using OpenAPI and includes dedicated documentation classes for the application's controllers.

---

# Repository Organization

This repository represents the **Read API / Query Side** of the URL Shortener system.

The complete system is composed of independent responsibilities:

```text
URL Shortener
│
├── Write API
│   └── Command Side
│
├── CDC Pipeline
│   └── Database → TiCDC → Kafka
│
└── Read API
    └── Query Side
        ├── Kafka Consumers
        ├── MongoDB Read Models
        └── Redis Cache
```

The separation makes it possible to evolve and scale the command and query sides independently.

---

# Architectural Patterns

The project applies or explores the following architectural and distributed-system patterns:

* CQRS
* Event-driven architecture
* Change Data Capture
* Read Models
* Asynchronous messaging
* Dead Letter Queue
* Retry processing
* Repository Pattern
* Dependency Inversion
* Layered Architecture
* Clean Architecture principles
* Hexagonal Architecture principles
* AOP for cross-cutting concerns
* Distributed ID generation
* Caching

---

# Project Goals

The project was created to explore the engineering challenges involved in building a distributed backend beyond a conventional CRUD architecture.

The main goals are:

1. Separate transactional writes from read-optimized workloads.
2. Build read models from CDC events.
3. Process asynchronous events reliably.
4. Handle event failures without data loss.
5. Support independent horizontal scaling.
6. Experiment with MongoDB as a query-oriented persistence layer.
7. Reduce read latency through Redis caching.
8. Apply production-oriented security and resilience practices.
9. Maintain a strong automated testing strategy.
10. Keep the architecture modular and maintainable.

---

# License

This project is intended primarily as a software engineering and architecture study project.
