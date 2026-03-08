package com.lobofoltran.order.interfaces.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class AnalyticsSummaryResponseTest {

    @Test
    fun `should store all fields correctly`() {
        val daily = listOf(
            DailyAnalyticsResponse(
                date = LocalDate.of(2026, 3, 7),
                totalOrders = 5,
                totalRevenue = BigDecimal("500.00"),
                averageTicket = BigDecimal("100.00")
            ),
            DailyAnalyticsResponse(
                date = LocalDate.of(2026, 3, 8),
                totalOrders = 10,
                totalRevenue = BigDecimal("1000.00"),
                averageTicket = BigDecimal("100.00")
            )
        )

        val response = AnalyticsSummaryResponse(
            totalOrders = 15,
            totalRevenue = BigDecimal("1500.00"),
            averageTicket = BigDecimal("100.00"),
            dailyBreakdown = daily
        )

        assertEquals(15, response.totalOrders)
        assertEquals(BigDecimal("1500.00"), response.totalRevenue)
        assertEquals(BigDecimal("100.00"), response.averageTicket)
        assertEquals(2, response.dailyBreakdown.size)
    }

    @Test
    fun `should handle empty daily breakdown`() {
        val response = AnalyticsSummaryResponse(
            totalOrders = 0,
            totalRevenue = BigDecimal.ZERO,
            averageTicket = BigDecimal.ZERO,
            dailyBreakdown = emptyList()
        )

        assertEquals(0, response.totalOrders)
        assertEquals(BigDecimal.ZERO, response.totalRevenue)
        assertEquals(0, response.dailyBreakdown.size)
    }
}

