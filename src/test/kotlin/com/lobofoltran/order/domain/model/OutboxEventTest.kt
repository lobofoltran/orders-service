package com.lobofoltran.order.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.UUID

class OutboxEventTest {

    @Test
    fun `should create outbox event with correct fields`() {
        val aggregateId = UUID.randomUUID()
        val payload = """{"orderId":"$aggregateId","total":100.0}"""

        val event = OutboxEvent.create(
            aggregateType = "Order",
            aggregateId = aggregateId,
            eventType = "OrderCreatedEvent",
            payload = payload
        )

        assertNotNull(event.id)
        assertEquals("Order", event.aggregateType)
        assertEquals(aggregateId, event.aggregateId)
        assertEquals("OrderCreatedEvent", event.eventType)
        assertEquals(payload, event.payload)
        assertNotNull(event.createdAt)
        assertNull(event.publishedAt)
        assertEquals(0, event.retries)
        assertNull(event.nextRetryAt)
    }

    @Test
    fun `should generate unique ids for different events`() {
        val event1 = OutboxEvent.create(
            aggregateType = "Order",
            aggregateId = UUID.randomUUID(),
            eventType = "OrderCreatedEvent",
            payload = "{}"
        )
        val event2 = OutboxEvent.create(
            aggregateType = "Order",
            aggregateId = UUID.randomUUID(),
            eventType = "OrderCreatedEvent",
            payload = "{}"
        )

        assertNotNull(event1.id)
        assertNotNull(event2.id)
        assert(event1.id != event2.id) { "Event IDs should be unique" }
    }
}

