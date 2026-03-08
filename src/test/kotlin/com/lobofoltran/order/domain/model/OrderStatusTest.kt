package com.lobofoltran.order.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OrderStatusTest {

    @Test
    fun `should have three statuses`() {
        assertEquals(3, OrderStatus.entries.size)
    }

    @Test
    fun `should contain CREATED status`() {
        assertEquals(OrderStatus.CREATED, OrderStatus.valueOf("CREATED"))
    }

    @Test
    fun `should contain PAID status`() {
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"))
    }

    @Test
    fun `should contain CANCELLED status`() {
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"))
    }
}

