# System Architecture

This document describes the architecture of the **Order Service**.

This is a **living document**.

---

## Overview

Architecture based on:

* Domain Driven Design
* Clean Architecture
* Event Driven Architecture

---

## Layers

```
Interfaces
Application
Domain
Infrastructure
```

---

## Interfaces Layer

Responsible for:

* REST API
* DTOs

---

## Application Layer

Responsible for:

* orchestrating use cases
* applying application rules

---

## Domain Layer

Contains:

* entities
* value objects
* events
* repository interfaces

---

## Infrastructure Layer

Contains:

* Hibernate
* RabbitMQ
* database
* external integrations

---

## Main flow

```
Client
  │
  ▼
REST Controller
  │
  ▼
CreateOrderUseCase (same @Transactional)
  │
  ├── Persist Order
  │
  └── Persist OutboxEvent
        │
        ▼
      DB COMMIT
```

Asynchronous worker:

```
OutboxEventPoller (@Scheduled every 500ms)
  │
  ▼
JPQL query with PESSIMISTIC_WRITE + SKIP LOCKED
  │
  ▼
Publish batch to RabbitMQ
  │
  ├── Success → batch mark as published
  │
  └── Failure → increment retries with backoff (next_retry_at = now + retries × 5s)
```

---

## Transactional Outbox Pattern

The system implements the **Transactional Outbox Pattern** to ensure
consistency between data persistence and event publishing.

### Problem solved

Eliminates the **dual write problem**: saving to the database and publishing to RabbitMQ
in separate operations can cause event loss if the broker fails.

### How it works

1. `CreateOrderUseCase` persists `Order` and `OutboxEvent` in the **same transaction**
2. `OutboxEventPoller` runs every 500ms, fetches pending events via **JPQL with PESSIMISTIC_WRITE + SKIP LOCKED**
3. For each event, publishes to RabbitMQ
4. On success, accumulates IDs and performs a **batch update** with `markBatchAsPublished`
5. On failure, increments `retries` and calculates `nextRetryAt` with backoff

### ORM-first

The implementation uses **Hibernate ORM (JPQL)** instead of native queries:

* `findPendingEvents` → JPQL with `setLockMode(PESSIMISTIC_WRITE)`
* `markAsPublished` → Hibernate dirty tracking via `entity.publishedAt = now`
* `markBatchAsPublished` → JPQL bulk update
* `incrementRetriesWithBackoff` → Hibernate dirty tracking
* `save` → `entityManager.persist()` without manual `flush()` (Hibernate batching)

### Concurrency

Uses `PESSIMISTIC_WRITE` with hint `jakarta.persistence.lock.timeout = -2`
which generates `FOR UPDATE SKIP LOCKED` on PostgreSQL, allowing multiple
poller instances without conflict (horizontal scaling).

### Retry Policy

* Max retries: 10
* Backoff: `next_retry_at = now + (retries + 1) × 5 seconds`
* Events with `retries >= 10` are ignored by the poller
* Events with `next_retry_at > now` are ignored until the cooldown expires

### Batch Processing

* Successfully published events are marked in bulk via JPQL bulk update
* Improves throughput under high load
* Configurable batch size (default: 200)

### Table

```sql
outbox_events (
    id, aggregate_type, aggregate_id, event_type,
    payload (JSONB), created_at, published_at, retries, next_retry_at
)
```

### Index

```sql
idx_outbox_pending ON outbox_events (published_at, next_retry_at, created_at)
WHERE published_at IS NULL
```

### Structure

```
infrastructure/
  ├── outbox/
  │   └── OutboxEventPoller.kt
  ├── persistence/
  │   ├── entity/OutboxEventEntity.kt
  │   ├── mapper/OutboxEventMapper.kt
  │   └── OutboxEventRepositoryImpl.kt
```

---

## Persistence

Technology:

```
PostgreSQL
```

ORM:

```
Hibernate
```

Migrations:

```
Flyway
```

---

## Messaging

Technology:

```
RabbitMQ
```

Exchange:

```
order.events
```

Main event:

```
OrderCreatedEvent
```

---

## Living Documentation

Files:

```
ARCHITECTURE.md
AGENTS.md
```

are considered **living documentation**.

Any architectural change must be reflected in these documents.

