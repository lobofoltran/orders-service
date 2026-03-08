package com.lobofoltran.order.interfaces.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.util.UUID

data class CreateOrderItemRequest(
    @field:NotNull(message = "productId is required")
    val productId: UUID?,

    @field:Min(value = 1, message = "quantity must be greater than zero")
    val quantity: Int = 0,

    @field:NotNull(message = "price is required")
    @field:DecimalMin(value = "0.01", message = "price must be greater than zero")
    val price: BigDecimal?
)

