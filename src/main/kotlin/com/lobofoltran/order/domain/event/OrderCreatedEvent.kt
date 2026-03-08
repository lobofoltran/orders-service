package com.lobofoltran.order.domain.event

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class OrderCreatedEvent(
    val orderId: UUID,
    val customerId: UUID,
    val total: BigDecimal,
    val createdAt: Instant
)
