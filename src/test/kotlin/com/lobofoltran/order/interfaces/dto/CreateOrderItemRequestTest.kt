package com.lobofoltran.order.interfaces.dto

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class CreateOrderItemRequestTest {

    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `should pass validation with valid data`() {
        val item = CreateOrderItemRequest(
            productId = UUID.randomUUID(),
            quantity = 1,
            price = BigDecimal("10.00")
        )
        val violations = validator.validate(item)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `should fail when productId is null`() {
        val item = CreateOrderItemRequest(
            productId = null,
            quantity = 1,
            price = BigDecimal("10.00")
        )
        val violations = validator.validate(item)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "productId" })
    }

    @Test
    fun `should fail when quantity is zero`() {
        val item = CreateOrderItemRequest(
            productId = UUID.randomUUID(),
            quantity = 0,
            price = BigDecimal("10.00")
        )
        val violations = validator.validate(item)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "quantity" })
    }

    @Test
    fun `should fail when price is null`() {
        val item = CreateOrderItemRequest(
            productId = UUID.randomUUID(),
            quantity = 1,
            price = null
        )
        val violations = validator.validate(item)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "price" })
    }

    @Test
    fun `should fail when price is zero`() {
        val item = CreateOrderItemRequest(
            productId = UUID.randomUUID(),
            quantity = 1,
            price = BigDecimal("0.00")
        )
        val violations = validator.validate(item)
        assertFalse(violations.isEmpty())
        assertTrue(violations.any { it.propertyPath.toString() == "price" })
    }
}

