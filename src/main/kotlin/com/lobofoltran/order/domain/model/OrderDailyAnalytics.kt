package com.lobofoltran.order.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class OrderDailyAnalytics(
    val date: LocalDate,
    val totalOrders: Long,
    val totalRevenue: BigDecimal,
    val updatedAt: Instant
) {
    val averageTicket: BigDecimal
        get() = if (totalOrders > 0) {
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
}

