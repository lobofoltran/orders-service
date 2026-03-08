package com.lobofoltran.order.application.usecase

import com.lobofoltran.order.domain.model.Order
import com.lobofoltran.order.domain.model.OrderItem
import com.lobofoltran.order.domain.model.OrderStatus
import com.lobofoltran.order.domain.model.OutboxEvent
import com.lobofoltran.order.domain.repository.OrderRepository
import com.lobofoltran.order.domain.repository.OutboxEventRepository
import io.vertx.core.json.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.UUID

class CreateOrderUseCaseTest {

    private val orderRepository: OrderRepository = mock()
    private val outboxEventRepository: OutboxEventRepository = mock()
    private val useCase = CreateOrderUseCase(orderRepository, outboxEventRepository)

    private fun validItem(quantity: Int = 2, price: BigDecimal = BigDecimal("10.00")) =
        OrderItem(productId = UUID.randomUUID(), quantity = quantity, price = price)

    @Test
    fun `should create order and persist it`() {
        val customerId = UUID.randomUUID()
        val items = listOf(validItem())

        whenever(orderRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Order>(0)
        }
        whenever(outboxEventRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<OutboxEvent>(0)
        }

        val result = useCase.execute(customerId, items)

        assertNotNull(result.id)
        assertEquals(customerId, result.customerId)
        assertEquals(OrderStatus.CREATED, result.status)
        assertEquals(BigDecimal("20.00"), result.total)
        verify(orderRepository).save(any())
    }

    @Test
    fun `should persist outbox event after saving order`() {
        val customerId = UUID.randomUUID()
        val items = listOf(validItem())

        whenever(orderRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Order>(0)
        }
        whenever(outboxEventRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<OutboxEvent>(0)
        }

        val result = useCase.execute(customerId, items)

        val eventCaptor = argumentCaptor<OutboxEvent>()
        verify(outboxEventRepository).save(eventCaptor.capture())

        val outboxEvent = eventCaptor.firstValue
        assertEquals("Order", outboxEvent.aggregateType)
        assertEquals(result.id, outboxEvent.aggregateId)
        assertEquals("OrderCreatedEvent", outboxEvent.eventType)

        val payload = JsonObject(outboxEvent.payload)
        assertEquals(result.id.toString(), payload.getString("orderId"))
        assertEquals(customerId.toString(), payload.getString("customerId"))
        assertEquals(result.total.toDouble(), payload.getDouble("total"))
        assertEquals(result.createdAt.toString(), payload.getString("createdAt"))
    }

    @Test
    fun `should calculate total correctly with multiple items`() {
        val customerId = UUID.randomUUID()
        val items = listOf(
            validItem(2, BigDecimal("10.00")),
            validItem(3, BigDecimal("5.00"))
        )

        whenever(orderRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Order>(0)
        }
        whenever(outboxEventRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<OutboxEvent>(0)
        }

        val result = useCase.execute(customerId, items)

        assertEquals(BigDecimal("35.00"), result.total)
    }

    @Test
    fun `should fail when items list is empty`() {
        val customerId = UUID.randomUUID()

        assertThrows<IllegalArgumentException> {
            useCase.execute(customerId, emptyList())
        }
    }
}

