# Order Service

Order microservice with event-driven architecture, implementing
**Transactional Outbox Pattern** and real-time **Analytics Projections**.

## Stack

| Technology      | Version  | Purpose                             |
|-----------------|----------|-------------------------------------|
| Kotlin          | 2.3      | Primary language                    |
| Quarkus         | 3.32     | Framework                           |
| PostgreSQL      | 17       | Database                            |
| RabbitMQ        | 3.13     | Messaging                           |
| Hibernate ORM   | —        | ORM (JPQL, dirty tracking, locking) |
| Flyway          | —        | Database migrations                 |
| JUnit 5         | —        | Testing                             |
| Mockito-Kotlin  | 5.4      | Mocks for unit tests                |
| JaCoCo          | —        | Code coverage                       |

## Architecture

The project follows **Clean Architecture** + **Domain Driven Design** + **Event Driven Architecture**.

### Layers

```
interfaces/        → REST controllers, DTOs
application/       → Use cases, services
domain/            → Entities, events, repository interfaces
infrastructure/    → Hibernate, RabbitMQ, persistence, outbox
```

### Main flow

```
Client → POST /orders
              │
              ▼
        OrderController
              │
              ▼
        CreateOrderUseCase (@Transactional)
              │
              ├── Persist Order
              └── Persist OutboxEvent
                        │
                        ▼
                    DB COMMIT
                        │
                        ▼
              OutboxEventPoller (@Scheduled 500ms)
                        │
                        ▼
              JPQL + PESSIMISTIC_WRITE + SKIP LOCKED
                        │
                        ▼
              Publish to RabbitMQ (batch)
                        │
                        ├── Success → batch mark as published
                        └── Failure → retry with backoff
                                          │
                                          ▼
                              OrderCreatedConsumer
                                          │
                                          ▼
                              OrderAnalyticsService
                                          │
                                ├── Daily Analytics (upsert)
                                ├── Customer Analytics (upsert)
                                └── Idempotency (processed_events)
```

## Project structure

```
src/main/kotlin/com/lobofoltran/order/
├── domain/
│   ├── event/
│   │   └── OrderCreatedEvent.kt
│   ├── model/
│   │   ├── Order.kt
│   │   ├── OrderItem.kt
│   │   ├── OrderStatus.kt
│   │   ├── OutboxEvent.kt
│   │   ├── OrderDailyAnalytics.kt
│   │   └── OrderCustomerAnalytics.kt
│   └── repository/
│       ├── OrderRepository.kt
│       ├── OutboxEventRepository.kt
│       ├── OrderDailyAnalyticsRepository.kt
│       ├── OrderCustomerAnalyticsRepository.kt
│       └── ProcessedAnalyticsEventRepository.kt
│
├── application/
│   ├── usecase/
│   │   └── CreateOrderUseCase.kt
│   └── service/
│       └── OrderAnalyticsService.kt
│
├── infrastructure/
│   ├── messaging/
│   │   ├── RabbitMQOrderEventPublisher.kt
│   │   └── OrderCreatedConsumer.kt
│   ├── outbox/
│   │   └── OutboxEventPoller.kt
│   └── persistence/
│       ├── entity/
│       │   ├── OrderEntity.kt
│       │   ├── OrderItemEntity.kt
│       │   ├── OutboxEventEntity.kt
│       │   ├── OrderDailyAnalyticsEntity.kt
│       │   ├── OrderCustomerAnalyticsEntity.kt
│       │   └── ProcessedAnalyticsEventEntity.kt
│       ├── mapper/
│       │   ├── OrderMapper.kt
│       │   ├── OutboxEventMapper.kt
│       │   ├── OrderDailyAnalyticsMapper.kt
│       │   └── OrderCustomerAnalyticsMapper.kt
│       ├── OrderRepositoryImpl.kt
│       ├── OutboxEventRepositoryImpl.kt
│       ├── OrderDailyAnalyticsRepositoryImpl.kt
│       ├── OrderCustomerAnalyticsRepositoryImpl.kt
│       └── ProcessedAnalyticsEventRepositoryImpl.kt
│
└── interfaces/
    ├── dto/
    │   ├── CreateOrderRequest.kt
    │   ├── CreateOrderItemRequest.kt
    │   ├── CreateOrderResponse.kt
    │   ├── DailyAnalyticsResponse.kt
    │   ├── CustomerAnalyticsResponse.kt
    │   └── AnalyticsSummaryResponse.kt
    └── rest/
        ├── OrderController.kt
        └── AnalyticsController.kt
```

## API

### Orders

#### Create order

```
POST /orders
```

```json
{
  "customerId": "uuid",
  "items": [
    {
      "productId": "uuid",
      "quantity": 2,
      "price": 10.00
    }
  ]
}
```

Response `201 Created`:

```json
{
  "orderId": "uuid"
}
```

### Analytics

#### Daily revenue

```
GET /analytics/daily
GET /analytics/daily?date=2026-03-08
```

Response `200 OK`:

```json
{
  "date": "2026-03-08",
  "totalOrders": 42,
  "totalRevenue": 5250.00,
  "averageTicket": 125.00
}
```

#### Customer analytics

```
GET /analytics/customer/{customerId}
```

Response `200 OK`:

```json
{
  "customerId": "uuid",
  "totalOrders": 7,
  "totalSpent": 890.50,
  "averageTicket": 127.21,
  "lastOrderAt": "2026-03-08T14:30:00Z"
}
```

#### Dashboard (summary)

```
GET /analytics/summary
```

Response `200 OK`:

```json
{
  "totalOrders": 150,
  "totalRevenue": 18750.00,
  "averageTicket": 125.00,
  "dailyBreakdown": [
    {
      "date": "2026-03-08",
      "totalOrders": 42,
      "totalRevenue": 5250.00,
      "averageTicket": 125.00
    }
  ]
}
```

## Transactional Outbox

Implementation of the **Transactional Outbox Pattern** to ensure consistency
between persistence and event publishing (eliminates the dual write problem).

### How it works

1. `CreateOrderUseCase` persists `Order` + `OutboxEvent` in the **same transaction**
2. `OutboxEventPoller` fetches pending events every **500ms** via **JPQL + PESSIMISTIC_WRITE + SKIP LOCKED**
3. Publishes to RabbitMQ and performs a **batch update** of published events
4. On failure, applies **retry with backoff** (`next_retry_at = now + retries × 5s`)

### Features

| Feature                  | Detail                                        |
|--------------------------|-----------------------------------------------|
| ORM-first                | JPQL instead of native SQL                    |
| Locking                  | `PESSIMISTIC_WRITE` + `SKIP LOCKED`           |
| Batch processing         | Configurable batch size (default: 200)        |
| Retry with backoff       | `next_retry_at = now + (retries + 1) × 5s`   |
| Max retries              | 10                                            |
| Concurrency              | `FOR UPDATE SKIP LOCKED` (horizontal scaling) |
| No manual flush          | Hibernate batching preserved                  |

## Analytics Projections

Event-based analytics system with projections updated in real time
from the `OrderCreatedEvent`.

### Flow

```
OrderCreatedEvent → RabbitMQ → OrderCreatedConsumer → OrderAnalyticsService → Analytics Tables
```

### Supported metrics

| Metric               | Table                        |
|----------------------|------------------------------|
| Orders per day       | `order_daily_analytics`      |
| Revenue per day      | `order_daily_analytics`      |
| Daily average ticket | Computed (revenue / orders)  |
| Orders per customer  | `order_customer_analytics`   |
| Total spent/customer | `order_customer_analytics`   |

### Idempotency

Duplicate events are ignored via the `processed_analytics_events` table
(primary key: `event_id = order_id`).

### Performance

- Updates are **incremental** (`INSERT ... ON CONFLICT DO UPDATE`)
- Complexity: **O(1)** per event (2 upserts + 1 check)

## Messaging

| Configuration | Value                        |
|---------------|------------------------------|
| Exchange      | `order.events`               |
| Routing Key   | `order.created`              |
| Queue         | `order.created.queue`        |
| Broker        | RabbitMQ 3.13                |

## Running locally

### Prerequisites

- Java 21
- Maven
- Docker and Docker Compose

### 1. Start infrastructure

```bash
docker compose up -d
```

Services started:

| Service    | Port   | UI                         |
|------------|--------|----------------------------|
| PostgreSQL | 5432   | —                          |
| RabbitMQ   | 5672   | http://localhost:15672      |

Default credentials: `orders / orders`

### 2. Start the application

```bash
mvn quarkus:dev
```

The application will be available at:

```
http://localhost:8080
```

### 3. Test it

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "productId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
        "quantity": 2,
        "price": 49.90
      }
    ]
  }'
```

```bash
curl http://localhost:8080/analytics/summary
```

## Testing

```bash
mvn test
```

Minimum required coverage: **80%**

Test types:

| Type        | Framework           | Scope                                        |
|-------------|---------------------|----------------------------------------------|
| Unit        | JUnit 5 + Mockito   | Use cases, services, mappers, domain models  |
| Integration | `@QuarkusTest`      | REST endpoints, persistence                  |

## Configuration

Main settings in `application.properties`:

```properties
# Outbox Poller
outbox.poll.interval=500ms
outbox.poll.batch-size=200
outbox.retry.max=10

# RabbitMQ
rabbitmq-host=localhost
rabbitmq-port=5672

# PostgreSQL
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/orders
```

## Living documentation

| File              | Content                                 |
|-------------------|-----------------------------------------|
| `README.md`       | Project overview                        |
| `ARCHITECTURE.md` | Detailed architecture                   |
| `AGENTS.md`       | Code generation agents specification    |

Any architectural change must be reflected in these documents.

