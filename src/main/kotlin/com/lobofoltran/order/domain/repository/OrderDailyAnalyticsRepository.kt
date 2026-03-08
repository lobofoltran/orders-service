package com.lobofoltran.order.domain.repository

import com.lobofoltran.order.domain.model.OrderDailyAnalytics
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

interface OrderDailyAnalyticsRepository {
    fun findByDate(date: LocalDate): OrderDailyAnalytics?
    fun upsert(date: LocalDate, revenue: BigDecimal, updatedAt: Instant)
    fun findAll(): List<OrderDailyAnalytics>
}

