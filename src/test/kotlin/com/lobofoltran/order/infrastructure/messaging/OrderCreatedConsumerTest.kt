package com.lobofoltran.order.infrastructure.messaging

import com.lobofoltran.order.application.service.OrderAnalyticsService
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderCreatedConsumerTest {

    private val analyticsService: OrderAnalyticsService = mock()
    private val consumer = OrderCreatedConsumer(analyticsService)

    @Test
    fun `should parse message and call analytics service`() {
        val orderId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val total = 150.0
        val createdAt = Instant.parse("2026-03-08T10:00:00Z")

        val message = JsonObject()
            .put("orderId", orderId.toString())
            .put("customerId", customerId.toString())
            .put("total", total)
            .put("createdAt", createdAt.toString())

        consumer.consume(message)

        verify(analyticsService).processOrder(
            eq(orderId),
            eq(customerId),
            eq(BigDecimal.valueOf(total)),
            eq(createdAt)
        )
    }

    @Test
    fun `should handle message with different total values`() {
        val orderId = UUID.randomUUID()
        val customerId = UUID.randomUUID()
        val total = 0.01
        val createdAt = Instant.parse("2026-01-01T00:00:00Z")

        val message = JsonObject()
            .put("orderId", orderId.toString())
            .put("customerId", customerId.toString())
            .put("total", total)
            .put("createdAt", createdAt.toString())

        consumer.consume(message)

        verify(analyticsService).processOrder(
            eq(orderId),
            eq(customerId),
            eq(BigDecimal.valueOf(total)),
            eq(createdAt)
        )
    }
}

