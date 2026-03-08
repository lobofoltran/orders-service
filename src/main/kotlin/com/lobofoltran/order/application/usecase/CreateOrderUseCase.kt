package com.lobofoltran.order.application.usecase

import com.lobofoltran.order.domain.event.OrderCreatedEvent
import com.lobofoltran.order.domain.model.Order
import com.lobofoltran.order.domain.model.OrderItem
import com.lobofoltran.order.domain.model.OutboxEvent
import com.lobofoltran.order.domain.repository.OrderRepository
import com.lobofoltran.order.domain.repository.OutboxEventRepository
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import java.util.UUID

@ApplicationScoped
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val outboxEventRepository: OutboxEventRepository
) {

    @Transactional
    fun execute(customerId: UUID, items: List<OrderItem>): Order {
        val order = Order.create(customerId, items)
        val savedOrder = orderRepository.save(order)

        val event = OrderCreatedEvent(
            orderId = savedOrder.id,
            customerId = savedOrder.customerId,
            total = savedOrder.total,
            createdAt = savedOrder.createdAt
        )

        val payload = JsonObject()
            .put("orderId", event.orderId.toString())
            .put("customerId", event.customerId.toString())
            .put("total", event.total.toDouble())
            .put("createdAt", event.createdAt.toString())
            .encode()

        val outboxEvent = OutboxEvent.create(
            aggregateType = "Order",
            aggregateId = savedOrder.id,
            eventType = "OrderCreatedEvent",
            payload = payload
        )
        outboxEventRepository.save(outboxEvent)

        return savedOrder
    }
}

