package com.lobofoltran.order.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class OrderDailyAnalyticsTest {

    @Test
    fun `should calculate average ticket correctly`() {
        val analytics = OrderDailyAnalytics(
            date = LocalDate.of(2026, 3, 8),
            totalOrders = 4,
            totalRevenue = BigDecimal("400.00"),
            updatedAt = Instant.now()
        )

        assertEquals(BigDecimal("100.00"), analytics.averageTicket)
    }

    @Test
    fun `should return zero average ticket when no orders`() {
        val analytics = OrderDailyAnalytics(
            date = LocalDate.of(2026, 3, 8),
            totalOrders = 0,
            totalRevenue = BigDecimal.ZERO,
            updatedAt = Instant.now()
        )

        assertEquals(BigDecimal.ZERO, analytics.averageTicket)
    }

    @Test
    fun `should calculate average ticket with rounding`() {
        val analytics = OrderDailyAnalytics(
            date = LocalDate.of(2026, 3, 8),
            totalOrders = 3,
            totalRevenue = BigDecimal("100.00"),
            updatedAt = Instant.now()
        )

        assertEquals(BigDecimal("33.33"), analytics.averageTicket)
    }

    @Test
    fun `should store all fields correctly`() {
        val date = LocalDate.of(2026, 3, 8)
        val updatedAt = Instant.now()

        val analytics = OrderDailyAnalytics(
            date = date,
            totalOrders = 10,
            totalRevenue = BigDecimal("1500.50"),
            updatedAt = updatedAt
        )

        assertEquals(date, analytics.date)
        assertEquals(10, analytics.totalOrders)
        assertEquals(BigDecimal("1500.50"), analytics.totalRevenue)
        assertEquals(updatedAt, analytics.updatedAt)
    }
}

