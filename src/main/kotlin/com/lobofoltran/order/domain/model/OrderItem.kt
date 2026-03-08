package com.lobofoltran.order.domain.model

import java.math.BigDecimal
import java.util.UUID

data class OrderItem(
    val productId: UUID,
    val quantity: Int,
    val price: BigDecimal
) {
    init {
        require(quantity > 0) { "Quantity must be greater than zero" }
        require(price > BigDecimal.ZERO) { "Price must be greater than zero" }
    }
}

