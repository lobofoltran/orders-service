package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.OrderDailyAnalytics
import com.lobofoltran.order.infrastructure.persistence.entity.OrderDailyAnalyticsEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

class OrderDailyAnalyticsMapperTest {

    @Test
    fun `should map entity to domain`() {
        val entity = OrderDailyAnalyticsEntity(
            date = LocalDate.of(2026, 3, 8),
            totalOrders = 10,
            totalRevenue = BigDecimal("1500.00"),
            updatedAt = Instant.parse("2026-03-08T12:00:00Z")
        )

        val domain = OrderDailyAnalyticsMapper.toDomain(entity)

        assertEquals(entity.date, domain.date)
        assertEquals(entity.totalOrders, domain.totalOrders)
        assertEquals(entity.totalRevenue, domain.totalRevenue)
        assertEquals(entity.updatedAt, domain.updatedAt)
    }

    @Test
    fun `should map domain to entity`() {
        val domain = OrderDailyAnalytics(
            date = LocalDate.of(2026, 3, 8),
            totalOrders = 10,
            totalRevenue = BigDecimal("1500.00"),
            updatedAt = Instant.parse("2026-03-08T12:00:00Z")
        )

        val entity = OrderDailyAnalyticsMapper.toEntity(domain)

        assertEquals(domain.date, entity.date)
        assertEquals(domain.totalOrders, entity.totalOrders)
        assertEquals(domain.totalRevenue, entity.totalRevenue)
        assertEquals(domain.updatedAt, entity.updatedAt)
    }

    @Test
    fun `should preserve zero values`() {
        val entity = OrderDailyAnalyticsEntity(
            date = LocalDate.of(2026, 1, 1),
            totalOrders = 0,
            totalRevenue = BigDecimal.ZERO,
            updatedAt = Instant.now()
        )

        val domain = OrderDailyAnalyticsMapper.toDomain(entity)

        assertEquals(0, domain.totalOrders)
        assertEquals(BigDecimal.ZERO, domain.totalRevenue)
    }
}

