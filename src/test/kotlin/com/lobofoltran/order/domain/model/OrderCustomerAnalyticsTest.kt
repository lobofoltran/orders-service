package com.lobofoltran.order.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderCustomerAnalyticsTest {

    @Test
    fun `should calculate average ticket correctly`() {
        val analytics = OrderCustomerAnalytics(
            customerId = UUID.randomUUID(),
            totalOrders = 5,
            totalSpent = BigDecimal("500.00"),
            lastOrderAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals(BigDecimal("100.00"), analytics.averageTicket)
    }

    @Test
    fun `should return zero average ticket when no orders`() {
        val analytics = OrderCustomerAnalytics(
            customerId = UUID.randomUUID(),
            totalOrders = 0,
            totalSpent = BigDecimal.ZERO,
            lastOrderAt = null,
            updatedAt = Instant.now()
        )

        assertEquals(BigDecimal.ZERO, analytics.averageTicket)
    }

    @Test
    fun `should calculate average ticket with rounding`() {
        val analytics = OrderCustomerAnalytics(
            customerId = UUID.randomUUID(),
            totalOrders = 3,
            totalSpent = BigDecimal("100.00"),
            lastOrderAt = Instant.now(),
            updatedAt = Instant.now()
        )

        assertEquals(BigDecimal("33.33"), analytics.averageTicket)
    }

    @Test
    fun `should allow null lastOrderAt`() {
        val analytics = OrderCustomerAnalytics(
            customerId = UUID.randomUUID(),
            totalOrders = 0,
            totalSpent = BigDecimal.ZERO,
            lastOrderAt = null,
            updatedAt = Instant.now()
        )

        assertNull(analytics.lastOrderAt)
    }

    @Test
    fun `should store all fields correctly`() {
        val customerId = UUID.randomUUID()
        val lastOrderAt = Instant.now()
        val updatedAt = Instant.now()

        val analytics = OrderCustomerAnalytics(
            customerId = customerId,
            totalOrders = 7,
            totalSpent = BigDecimal("999.99"),
            lastOrderAt = lastOrderAt,
            updatedAt = updatedAt
        )

        assertEquals(customerId, analytics.customerId)
        assertEquals(7, analytics.totalOrders)
        assertEquals(BigDecimal("999.99"), analytics.totalSpent)
        assertEquals(lastOrderAt, analytics.lastOrderAt)
        assertEquals(updatedAt, analytics.updatedAt)
    }
}

