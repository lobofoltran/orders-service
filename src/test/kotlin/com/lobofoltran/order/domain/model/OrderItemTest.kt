package com.lobofoltran.order.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

class OrderItemTest {

    @Test
    fun `should create order item with valid data`() {
        val item = OrderItem(
            productId = UUID.randomUUID(),
            quantity = 2,
            price = BigDecimal("10.00")
        )
        assertNotNull(item)
        assertEquals(2, item.quantity)
        assertEquals(BigDecimal("10.00"), item.price)
    }

    @Test
    fun `should fail when quantity is zero`() {
        val exception = assertThrows<IllegalArgumentException> {
            OrderItem(
                productId = UUID.randomUUID(),
                quantity = 0,
                price = BigDecimal("10.00")
            )
        }
        assertEquals("Quantity must be greater than zero", exception.message)
    }

    @Test
    fun `should fail when quantity is negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            OrderItem(
                productId = UUID.randomUUID(),
                quantity = -1,
                price = BigDecimal("10.00")
            )
        }
        assertEquals("Quantity must be greater than zero", exception.message)
    }

    @Test
    fun `should fail when price is zero`() {
        val exception = assertThrows<IllegalArgumentException> {
            OrderItem(
                productId = UUID.randomUUID(),
                quantity = 1,
                price = BigDecimal.ZERO
            )
        }
        assertEquals("Price must be greater than zero", exception.message)
    }

    @Test
    fun `should fail when price is negative`() {
        val exception = assertThrows<IllegalArgumentException> {
            OrderItem(
                productId = UUID.randomUUID(),
                quantity = 1,
                price = BigDecimal("-5.00")
            )
        }
        assertEquals("Price must be greater than zero", exception.message)
    }
}

