package com.lobofoltran.order.interfaces.dto

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class CreateOrderRequestTest {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    private fun validItemRequest() = CreateOrderItemRequest(
        productId = UUID.randomUUID(),
        quantity = 2,
        price = BigDecimal("10.00")
    )

    @Test
    fun `should pass validation with valid data`() {
        val request = CreateOrderRequest(
            customerId = UUID.randomUUID(),
            items = listOf(validItemRequest())
        )
        val violations = validator.validate(request)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `should fail validation when customerId is null`() {
        val request = CreateOrderRequest(
            customerId = null,
            items = listOf(validItemRequest())
        )
        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "customerId" })
    }

    @Test
    fun `should fail validation when items is empty`() {
        val request = CreateOrderRequest(
            customerId = UUID.randomUUID(),
            items = emptyList()
        )
        val violations = validator.validate(request)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "items" })
    }
}

