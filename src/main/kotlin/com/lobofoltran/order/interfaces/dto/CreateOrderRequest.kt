package com.lobofoltran.order.interfaces.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateOrderRequest(
    @field:NotNull(message = "customerId is required")
    val customerId: UUID?,

    @field:NotEmpty(message = "items must not be empty")
    @field:Valid
    val items: List<CreateOrderItemRequest> = emptyList()
)

