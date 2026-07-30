# Financial Settlement System

![Java 21](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)
![Apache Kafka](https://img.shields.io/badge/Kafka-Confluent%207.6-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)

A financial settlement system built with **Clean Architecture**, **Java 21**, **Spring Boot 3.3.5**, **Apache Kafka**, and **PostgreSQL**.

## Architecture

```
┌──────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                    │
│  ┌─────────┐  ┌──────────┐  ┌────────────┐  ┌────────┐  │
│  │  REST   │  │  Kafka   │  │    JPA     │  │  Flyway │  │
│  │ Adapter │  │ Consumer │  │ Repository │  │  Migr.  │  │
│  └────┬────┘  └────┬─────┘  └─────┬──────┘  └────────┘  │
│       │            │              │                       │
├───────┴────────────┴──────────────┴──────────────────────┤
│                  Application Layer                        │
│  ┌─────────────┐  ┌──────────┐  ┌───────────────────┐   │
│  │  Use Cases  │  │  Events  │  │  Ports (in/out)   │   │
│  └──────┬──────┘  └────┬─────┘  └────────┬──────────┘   │
│         │              │                  │               │
├─────────┴──────────────┴──────────────────┴──────────────┤
│                   Domain Layer                            │
│  ┌──────────┐  ┌────────────┐  ┌───────────────────┐    │
│  │ Entities │  │Value Objects│  │  Domain Services  │    │
│  └──────────┘  └────────────┘  └───────────────────┘    │
└──────────────────────────────────────────────────────────┘
```

## API Endpoints

| Method | Path                    | Description              |
|--------|-------------------------|--------------------------|
| POST   | /api/payments           | Create a new payment     |
| GET    | /api/payments/{id}      | Get payment by ID        |
| GET    | /api/payments/account/{accountId} | Get payments by account |

### Request Body (POST /api/payments)

```json
{
  "accountId": "ACC-001",
  "amount": 1500.00,
  "currency": "USD",
  "description": "Invoice payment"
}
```

### Response

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "accountId": "ACC-001",
  "amount": 1500.00,
  "currency": "USD",
  "status": "PENDING",
  "createdAt": "2026-07-28T22:00:00Z"
}
```

## Setup

### Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven 3.9+

### Running with Docker Compose

```bash
docker-compose up -d
```

### Running locally

```bash
# Start dependencies
docker-compose up -d postgres zookeeper kafka

# Build and run
mvn clean package -DskipTests
java -XX:+UseZGC -XX:+ZGenerational -jar target/financial-settlement-system-1.0.0.jar
```

### Environment Variables

| Variable                          | Default                                      | Description         |
|-----------------------------------|----------------------------------------------|---------------------|
| SPRING_KAFKA_BOOTSTRAP_SERVERS    | localhost:9092                               | Kafka broker(s)     |
| SPRING_DATASOURCE_URL             | jdbc:postgresql://localhost:5432/...         | PostgreSQL JDBC URL |
| SPRING_DATASOURCE_USERNAME        | settlement                                   | DB username         |
| SPRING_DATASOURCE_PASSWORD        | settlement123                                | DB password         |

### Running tests

```bash
mvn verify
```

## Technology Stack

- **Java 21** — Records, Sealed Classes, Pattern Matching, Virtual Threads
- **Spring Boot 3.3.5** — Web, JPA, Kafka, Validation, Actuator
- **Apache Kafka Confluent 7.6** — Event streaming
- **PostgreSQL 16** — Persistence
- **Flyway** — Database migrations
- **Testcontainers** — Integration testing
- **JaCoCo** — Code coverage
