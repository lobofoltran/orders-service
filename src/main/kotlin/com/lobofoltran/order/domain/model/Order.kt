package com.lobofoltran.order.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Order(
    val id: UUID,
    val customerId: UUID,
    val status: OrderStatus,
    val items: List<OrderItem>,
    val total: BigDecimal,
    val createdAt: Instant
) {
    init {
        require(items.isNotEmpty()) { "Order must have at least one item" }
        require(total > BigDecimal.ZERO) { "Order total must be greater than zero" }
    }

    companion object {
        fun create(customerId: UUID, items: List<OrderItem>): Order {
            require(items.isNotEmpty()) { "Order must have at least one item" }
            val total = items.fold(BigDecimal.ZERO) { acc, item ->
                acc + (item.price * BigDecimal(item.quantity))
            }
            return Order(
                id = UUID.randomUUID(),
                customerId = customerId,
                status = OrderStatus.CREATED,
                items = items,
                total = total,
                createdAt = Instant.now()
            )
        }
    }
}

