package com.lobofoltran.order.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class OrderCustomerAnalytics(
    val customerId: UUID,
    val totalOrders: Long,
    val totalSpent: BigDecimal,
    val lastOrderAt: Instant?,
    val updatedAt: Instant
) {
    val averageTicket: BigDecimal
        get() = if (totalOrders > 0) {
            totalSpent.divide(BigDecimal.valueOf(totalOrders), 2, java.math.RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }
}

