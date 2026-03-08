package com.lobofoltran.order.interfaces.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class DailyAnalyticsResponseTest {

    @Test
    fun `should store all fields correctly`() {
        val response = DailyAnalyticsResponse(
            date = LocalDate.of(2026, 3, 8),
            totalOrders = 10,
            totalRevenue = BigDecimal("1500.00"),
            averageTicket = BigDecimal("150.00")
        )

        assertEquals(LocalDate.of(2026, 3, 8), response.date)
        assertEquals(10, response.totalOrders)
        assertEquals(BigDecimal("1500.00"), response.totalRevenue)
        assertEquals(BigDecimal("150.00"), response.averageTicket)
    }

    @Test
    fun `should support zero values`() {
        val response = DailyAnalyticsResponse(
            date = LocalDate.of(2026, 1, 1),
            totalOrders = 0,
            totalRevenue = BigDecimal.ZERO,
            averageTicket = BigDecimal.ZERO
        )

        assertEquals(0, response.totalOrders)
        assertEquals(BigDecimal.ZERO, response.totalRevenue)
        assertEquals(BigDecimal.ZERO, response.averageTicket)
    }
}

