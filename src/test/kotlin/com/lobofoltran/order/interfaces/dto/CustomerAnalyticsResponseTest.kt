package com.lobofoltran.order.interfaces.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class CustomerAnalyticsResponseTest {

    @Test
    fun `should store all fields correctly`() {
        val customerId = UUID.randomUUID()
        val lastOrderAt = Instant.parse("2026-03-08T10:00:00Z")

        val response = CustomerAnalyticsResponse(
            customerId = customerId,
            totalOrders = 5,
            totalSpent = BigDecimal("500.00"),
            averageTicket = BigDecimal("100.00"),
            lastOrderAt = lastOrderAt
        )

        assertEquals(customerId, response.customerId)
        assertEquals(5, response.totalOrders)
        assertEquals(BigDecimal("500.00"), response.totalSpent)
        assertEquals(BigDecimal("100.00"), response.averageTicket)
        assertEquals(lastOrderAt, response.lastOrderAt)
    }

    @Test
    fun `should allow null lastOrderAt`() {
        val response = CustomerAnalyticsResponse(
            customerId = UUID.randomUUID(),
            totalOrders = 0,
            totalSpent = BigDecimal.ZERO,
            averageTicket = BigDecimal.ZERO,
            lastOrderAt = null
        )

        assertNull(response.lastOrderAt)
    }
}

