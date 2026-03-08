package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.OrderCustomerAnalytics
import com.lobofoltran.order.infrastructure.persistence.entity.OrderCustomerAnalyticsEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderCustomerAnalyticsMapperTest {

    @Test
    fun `should map entity to domain`() {
        val entity = OrderCustomerAnalyticsEntity(
            customerId = UUID.randomUUID(),
            totalOrders = 5,
            totalSpent = BigDecimal("500.00"),
            lastOrderAt = Instant.parse("2026-03-08T12:00:00Z"),
            updatedAt = Instant.parse("2026-03-08T12:00:00Z")
        )

        val domain = OrderCustomerAnalyticsMapper.toDomain(entity)

        assertEquals(entity.customerId, domain.customerId)
        assertEquals(entity.totalOrders, domain.totalOrders)
        assertEquals(entity.totalSpent, domain.totalSpent)
        assertEquals(entity.lastOrderAt, domain.lastOrderAt)
        assertEquals(entity.updatedAt, domain.updatedAt)
    }

    @Test
    fun `should map domain to entity`() {
        val domain = OrderCustomerAnalytics(
            customerId = UUID.randomUUID(),
            totalOrders = 5,
            totalSpent = BigDecimal("500.00"),
            lastOrderAt = Instant.parse("2026-03-08T12:00:00Z"),
            updatedAt = Instant.parse("2026-03-08T12:00:00Z")
        )

        val entity = OrderCustomerAnalyticsMapper.toEntity(domain)

        assertEquals(domain.customerId, entity.customerId)
        assertEquals(domain.totalOrders, entity.totalOrders)
        assertEquals(domain.totalSpent, entity.totalSpent)
        assertEquals(domain.lastOrderAt, entity.lastOrderAt)
        assertEquals(domain.updatedAt, entity.updatedAt)
    }

    @Test
    fun `should handle null lastOrderAt`() {
        val entity = OrderCustomerAnalyticsEntity(
            customerId = UUID.randomUUID(),
            totalOrders = 0,
            totalSpent = BigDecimal.ZERO,
            lastOrderAt = null,
            updatedAt = Instant.now()
        )

        val domain = OrderCustomerAnalyticsMapper.toDomain(entity)

        assertNull(domain.lastOrderAt)
    }
}

