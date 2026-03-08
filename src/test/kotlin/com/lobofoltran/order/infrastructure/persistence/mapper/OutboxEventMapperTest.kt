package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.OutboxEvent
import com.lobofoltran.order.infrastructure.persistence.entity.OutboxEventEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class OutboxEventMapperTest {

    @Test
    fun `should map domain to entity`() {
        val event = OutboxEvent(
            id = UUID.randomUUID(),
            aggregateType = "Order",
            aggregateId = UUID.randomUUID(),
            eventType = "OrderCreatedEvent",
            payload = """{"orderId":"abc"}""",
            createdAt = Instant.now(),
            publishedAt = null, retries = 0,
            nextRetryAt = null
        )

        val entity = OutboxEventMapper.toEntity(event)

        assertEquals(event.id, entity.id)
        assertEquals(event.aggregateType, entity.aggregateType)
        assertEquals(event.aggregateId, entity.aggregateId)
        assertEquals(event.eventType, entity.eventType)
        assertEquals(event.payload, entity.payload)
        assertEquals(event.createdAt, entity.createdAt)
        assertNull(entity.publishedAt)
        assertEquals(0, entity.retries)
        assertNull(entity.nextRetryAt)
    }

    @Test
    fun `should map entity to domain`() {
        val nextRetry = Instant.now().plusSeconds(15)
        val entity = OutboxEventEntity(
            id = UUID.randomUUID(),
            aggregateType = "Order",
            aggregateId = UUID.randomUUID(),
            eventType = "OrderCreatedEvent",
            payload = """{"orderId":"abc"}""",
            createdAt = Instant.now(),
            publishedAt = Instant.now(),
            retries = 3,
            nextRetryAt = nextRetry
        )

        val domain = OutboxEventMapper.toDomain(entity)

        assertEquals(entity.id, domain.id)
        assertEquals(entity.aggregateType, domain.aggregateType)
        assertEquals(entity.aggregateId, domain.aggregateId)
        assertEquals(entity.eventType, domain.eventType)
        assertEquals(entity.payload, domain.payload)
        assertEquals(entity.createdAt, domain.createdAt)
        assertEquals(entity.publishedAt, domain.publishedAt)
        assertEquals(3, domain.retries)
        assertEquals(nextRetry, domain.nextRetryAt)
    }

    @Test
    fun `should preserve null publishedAt and nextRetryAt in round-trip`() {
        val event = OutboxEvent.create(
            aggregateType = "Order",
            aggregateId = UUID.randomUUID(),
            eventType = "OrderCreatedEvent",
            payload = "{}"
        )

        val entity = OutboxEventMapper.toEntity(event)
        val result = OutboxEventMapper.toDomain(entity)

        assertEquals(event.id, result.id)
        assertEquals(event.aggregateType, result.aggregateType)
        assertEquals(event.aggregateId, result.aggregateId)
        assertEquals(event.eventType, result.eventType)
        assertEquals(event.payload, result.payload)
        assertNull(result.publishedAt)
        assertEquals(0, result.retries)
        assertNull(result.nextRetryAt)
    }

    @Test
    fun `should map nextRetryAt correctly in round-trip`() {
        val nextRetry = Instant.now().plusSeconds(30)
        val event = OutboxEvent(
            id = UUID.randomUUID(),
            aggregateType = "Order",
            aggregateId = UUID.randomUUID(),
            eventType = "OrderCreatedEvent",
            payload = """{"test":true}""",
            createdAt = Instant.now(),
            publishedAt = null,
            retries = 5,
            nextRetryAt = nextRetry
        )

        val entity = OutboxEventMapper.toEntity(event)
        val result = OutboxEventMapper.toDomain(entity)

        assertEquals(5, result.retries)
        assertEquals(nextRetry, result.nextRetryAt)
    }
}

