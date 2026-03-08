package com.lobofoltran.order.interfaces.dto

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class CustomerAnalyticsResponse(
    val customerId: UUID,
    val totalOrders: Long,
    val totalSpent: BigDecimal,
    val averageTicket: BigDecimal,
    val lastOrderAt: Instant?
)

