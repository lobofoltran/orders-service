package com.lobofoltran.order.domain.event

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderCreatedEventTest {

    @Test
    fun `should create event with all fields`() {
        val orderId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val total = BigDecimal("100.00")
        val createdAt = Instant.now()

        val event = OrderCreatedEvent(
            orderId = orderId,
            customerId = customerId,
            total = total,
            createdAt = createdAt
        )

        assertEquals(orderId, event.orderId)
        assertEquals(customerId, event.customerId)
        assertEquals(total, event.total)
        assertEquals(createdAt, event.createdAt)
    }

    @Test
    fun `should support data class equality`() {
        val orderId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val total = BigDecimal("50.00")
        val createdAt = Instant.now()

        val event1 = OrderCreatedEvent(orderId, customerId, total, createdAt)
        val event2 = OrderCreatedEvent(orderId, customerId, total, createdAt)

        assertEquals(event1, event2)
    }
}

