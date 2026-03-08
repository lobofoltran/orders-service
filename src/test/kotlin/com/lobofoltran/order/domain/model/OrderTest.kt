package com.lobofoltran.order.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderTest {

    private fun validItem(quantity: Int = 2, price: BigDecimal = BigDecimal("10.00")) =
        OrderItem(productId = UUID.randomUUID(), quantity = quantity, price = price)

    @Test
    fun `should create order via factory method`() {
        val customerId = UUID.randomUUID()
        val items = listOf(validItem(2, BigDecimal("10.00")))

        val order = Order.create(customerId, items)

        assertNotNull(order.id)
        assertEquals(customerId, order.customerId)
        assertEquals(OrderStatus.CREATED, order.status)
        assertEquals(1, order.items.size)
        assertEquals(BigDecimal("20.00"), order.total)
        assertNotNull(order.createdAt)
    }

    @Test
    fun `should calculate total with multiple items`() {
        val items = listOf(
            validItem(2, BigDecimal("10.00")),
            validItem(3, BigDecimal("5.00"))
        )

        val order = Order.create(UUID.randomUUID(), items)

        // 2*10 + 3*5 = 35
        assertEquals(BigDecimal("35.00"), order.total)
    }

    @Test
    fun `should fail when items list is empty via factory`() {
        val exception = assertThrows<IllegalArgumentException> {
            Order.create(UUID.randomUUID(), emptyList())
        }
        assertEquals("Order must have at least one item", exception.message)
    }

    @Test
    fun `should fail when items list is empty via constructor`() {
        val exception = assertThrows<IllegalArgumentException> {
            Order(
                id = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                status = OrderStatus.CREATED,
                items = emptyList(),
                total = BigDecimal("10.00"),
                createdAt = Instant.now()
            )
        }
        assertEquals("Order must have at least one item", exception.message)
    }

    @Test
    fun `should fail when total is zero`() {
        val exception = assertThrows<IllegalArgumentException> {
            Order(
                id = UUID.randomUUID(),
                customerId = UUID.randomUUID(),
                status = OrderStatus.CREATED,
                items = listOf(validItem()),
                total = BigDecimal.ZERO,
                createdAt = Instant.now()
            )
        }
        assertEquals("Order total must be greater than zero", exception.message)
    }

    @Test
    fun `should set status to CREATED on creation`() {
        val order = Order.create(UUID.randomUUID(), listOf(validItem()))
        assertEquals(OrderStatus.CREATED, order.status)
    }
}

