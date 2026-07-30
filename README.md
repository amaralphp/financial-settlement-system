# Financial Settlement System

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?logo=spring)
![Apache Kafka](https://img.shields.io/badge/Kafka-Confluent%207.6-blue?logo=apachekafka)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)
![Flyway](https://img.shields.io/badge/Flyway-10-red?logo=flyway)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![JaCoCo](https://img.shields.io/badge/Cobertura-80%25-yellowgreen)
![Micrometer](https://img.shields.io/badge/Micrometer-1.13-brightgreen)
![Prometheus](https://img.shields.io/badge/Prometheus-2.53-E6522C)
![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-1.37-4A154B?logo=opentelemetry)

**Sistema de liquidação financeira** para processamento de pagamentos com arquitetura orientada a eventos. Construído com Clean Architecture, Kafka para fluxo de eventos e PostgreSQL para persistência transacional.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Stack Tecnológica](#stack-tecnológica)
- [Pré-requisitos](#pré-requisitos)
- [Início Rápido](#início-rápido)
- [Endpoints da API](#endpoints-da-api)
- [Fluxo de Pagamento](#fluxo-de-pagamento)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Monitoramento](#monitoramento)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Contribuição](#contribuição)
- [Licença](#licença)

---

## Visão Geral

O Financial Settlement System processa pagamentos de forma assíncrona e resiliente. Cada transação passa por validação de domínio, persistência segura e publicação de eventos para downstream consumers. O uso de Kafka garante rastreabilidade total e capacidade de reprocessamento.

### Casos de Uso

- Criação e processamento de pagamentos
- Consulta de transações por ID e por conta
- Reconciliação financeira
- Notificação de eventos de pagamento para sistemas externos
- Trilha de auditoria completa via Kafka

---

## Arquitetura

```
┌──────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                      │
│  ┌─────────┐  ┌──────────┐  ┌────────────┐  ┌────────┐  │
│  │  REST   │  │  Kafka   │  │    JPA     │  │  Flyway │  │
│  │ Adapter │  │ Consumer │  │ Repository │  │  Migr.  │  │
│  └────┬────┘  └────┬─────┘  └─────┬──────┘  └────────┘  │
│       │            │              │                       │
├───────┴────────────┴──────────────┴──────────────────────┤
│                  Application Layer                         │
│  ┌─────────────┐  ┌──────────┐  ┌───────────────────┐    │
│  │  Use Cases  │  │  Events  │  │  Ports (in/out)   │    │
│  └──────┬──────┘  └────┬─────┘  └────────┬──────────┘    │
│         │              │                  │               │
├─────────┴──────────────┴──────────────────┴──────────────┤
│                   Domain Layer                             │
│  ┌──────────┐  ┌────────────┐  ┌───────────────────┐     │
│  │ Entities │  │Value Objects│  │  Domain Services  │     │
│  └──────────┘  └────────────┘  └───────────────────┘     │
└──────────────────────────────────────────────────────────┘
```

### Camadas

| Camada | Responsabilidade |
|--------|-----------------|
| **Domain** | Entidades, value objects, regras de negócio (juros, multas, status) |
| **Application** | Casos de uso, portas de entrada/saída, eventos de aplicação |
| **Infrastructure** | REST, Kafka, JPA, Flyway — implementações concretas |

---

## Funcionalidades

- **Pagamentos Assíncronos** — Criação e processamento via eventos Kafka
- **Status Tracking** — Ciclo completo: PENDING → PROCESSING → COMPLETED / FAILED
- **Reconciliação** — Mecanismo para conciliar transações com sistemas externos
- **Virtual Threads** — Java 21 Virtual Threads para alta concorrência
- **ZGC** — Garbage Collector ZGC Generational para baixa latência
- **Flyway Migrations** — Controle de versão do schema PostgreSQL
- **Clean Architecture** — Domínio totalmente isolado de frameworks
- **Testcontainers** — Testes de integração com PostgreSQL e Kafka reais

---

## Stack Tecnológica

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java | 21 | Records, Virtual Threads, ZGC |
| Spring Boot | 3.3.5 | Web, JPA, Kafka, Validation, Actuator |
| Apache Kafka | Confluent 7.6 | Stream de eventos de pagamento |
| PostgreSQL | 16 | Persistência transacional |
| Flyway | 10 | Migrações de banco |
| Testcontainers | 1.19.8 | Testes de integração |
| JaCoCo | 0.8.11 | Cobertura de código |
| Docker | Compose V2 | Infraestrutura local |
| Prometheus | 2.53 | Métricas e monitoramento |
| OpenTelemetry | 1.37 | Distributed tracing |
| Zipkin | 3.4 | Trace visualization |
| Jaeger | 1.60 | Trace visualization (OTLP) |

---

## Pré-requisitos

- **JDK 21+**
- **Docker & Docker Compose**
- **Maven 3.9+**

---

## Início Rápido

### Com Docker Compose (stack completo)

```bash
docker-compose up -d
```

### Local (desenvolvimento)

```bash
# Iniciar dependências
docker-compose up -d postgres zookeeper kafka

# Build
mvn clean package -DskipTests

# Executar com ZGC para baixa latência
java -XX:+UseZGC -XX:+ZGenerational \
  -jar target/financial-settlement-system-1.0.0.jar
```

### Verificar

```bash
curl http://localhost:8080/actuator/health
```

---

## Endpoints da API

| Método | Path | Descrição |
|--------|------|-----------|
| `POST` | `/api/payments` | Criar novo pagamento |
| `GET` | `/api/payments/{id}` | Obter pagamento por ID |
| `GET` | `/api/payments/account/{accountId}` | Listar pagamentos por conta |

### Exemplo: Criar Pagamento

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "ACC-001",
    "amount": 1500.00,
    "currency": "BRL",
    "description": "Pagamento de fatura"
  }'
```

**Resposta:**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "accountId": "ACC-001",
  "amount": 1500.00,
  "currency": "BRL",
  "status": "PENDING",
  "createdAt": "2026-07-28T22:00:00Z"
}
```

---

## Fluxo de Pagamento

```
Cliente → POST /api/payments
              ↓
    ExecutePaymentUseCase
              ↓
    Valida regras de domínio
              ↓
    TransactionRepository.save()
              ↓
    KafkaEventPublisher.publish()
              ↓
    PaymentEventConsumer.process()
              ↓
    Atualiza status → COMPLETED
              ↓
    Evento de conclusão publicado
```

---

## Estrutura do Projeto

```
src/main/java/com/financialsettlement/
├── domain/
│   ├── entity/              # Entidades
│   │   ├── Payment.java
│   │   └── Account.java
│   ├── vo/                  # Value Objects
│   │   ├── Money.java
│   │   └── PaymentStatus.java
│   └── service/             # Regras de negócio
├── application/
│   ├── port/
│   │   ├── input/           # Casos de uso
│   │   └── output/          # Repositórios
│   └── usecase/             # Implementações
└── infrastructure/
    ├── adapter/
    │   ├── rest/            # Controllers
    │   ├── kafka/           # Eventos
    │   └── persistence/     # JPA + Flyway
    └── config/              # Beans
```

---

## Testes

```bash
# Todos os testes
mvn test

# Com cobertura
mvn verify

# Relatório JaCoCo
open target/site/jacoco/index.html
```

---

## Monitoramento

| Endpoint | Descrição |
|----------|-----------|
| `GET /actuator/health` | Health check (PostgreSQL, Kafka) |
| `GET /actuator/info` | Informações da aplicação |
| `GET /actuator/metrics` | Métricas Micrometer |
| `GET /actuator/prometheus` | Métricas no formato Prometheus |

### Tracing Distribuído

Tracing via OpenTelemetry com exportação para Zipkin e Jaeger.

---

## Variáveis de Ambiente

| Variável | Default | Descrição |
|----------|---------|-----------|
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Broker Kafka |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/settlement` | URL JDBC |
| `SPRING_DATASOURCE_USERNAME` | `settlement` | Usuário |
| `SPRING_DATASOURCE_PASSWORD` | `settlement123` | Senha |
| `SERVER_PORT` | `8080` | Porta HTTP |

---

## Contribuição

1. Fork o projeto
2. Crie sua branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

---

## Licença

MIT
