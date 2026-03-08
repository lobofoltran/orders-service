package com.lobofoltran.order.interfaces.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateOrderResponseTest {

    @Test
    fun `should create response with orderId`() {
        val orderId = UUID.randomUUID()
        val response = CreateOrderResponse(orderId = orderId)
        assertEquals(orderId, response.orderId)
    }
}

