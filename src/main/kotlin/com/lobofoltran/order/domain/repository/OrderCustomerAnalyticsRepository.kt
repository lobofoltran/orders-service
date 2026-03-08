package com.lobofoltran.order.domain.repository

import com.lobofoltran.order.domain.model.OrderCustomerAnalytics
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

interface OrderCustomerAnalyticsRepository {
    fun findByCustomerId(customerId: UUID): OrderCustomerAnalytics?
    fun upsert(customerId: UUID, revenue: BigDecimal, orderAt: Instant, updatedAt: Instant)
}

