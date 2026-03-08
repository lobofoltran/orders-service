package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.Order
import com.lobofoltran.order.domain.model.OrderItem
import com.lobofoltran.order.domain.model.OrderStatus
import com.lobofoltran.order.infrastructure.persistence.entity.OrderEntity
import com.lobofoltran.order.infrastructure.persistence.entity.OrderItemEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class OrderMapperTest {

    @Test
    fun `should map domain order to entity`() {
        val order = Order(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            status = OrderStatus.CREATED,
            items = listOf(
                OrderItem(productId = UUID.randomUUID(), quantity = 2, price = BigDecimal("10.00"))
            ),
            total = BigDecimal("20.00"),
            createdAt = Instant.now()
        )

        val entity = OrderMapper.toEntity(order)

        assertEquals(order.id, entity.id)
        assertEquals(order.customerId, entity.customerId)
        assertEquals(order.status, entity.status)
        assertEquals(order.total, entity.total)
        assertEquals(order.createdAt, entity.createdAt)
        assertEquals(1, entity.items.size)
        assertEquals(order.items[0].productId, entity.items[0].productId)
        assertEquals(order.items[0].quantity, entity.items[0].quantity)
        assertEquals(order.items[0].price, entity.items[0].price)
        assertEquals(entity, entity.items[0].order)
    }

    @Test
    fun `should map entity to domain order`() {
        val orderEntity = OrderEntity(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            status = OrderStatus.CREATED,
            total = BigDecimal("20.00"),
            createdAt = Instant.now()
        )
        val itemEntity = OrderItemEntity(
            productId = UUID.randomUUID(),
            quantity = 2,
            price = BigDecimal("10.00"),
            order = orderEntity
        )
        orderEntity.items = mutableListOf(itemEntity)

        val order = OrderMapper.toDomain(orderEntity)

        assertEquals(orderEntity.id, order.id)
        assertEquals(orderEntity.customerId, order.customerId)
        assertEquals(OrderStatus.CREATED, order.status)
        assertEquals(orderEntity.total, order.total)
        assertEquals(orderEntity.createdAt, order.createdAt)
        assertEquals(1, order.items.size)
        assertEquals(itemEntity.productId, order.items[0].productId)
        assertEquals(itemEntity.quantity, order.items[0].quantity)
        assertEquals(itemEntity.price, order.items[0].price)
    }

    @Test
    fun `should map multiple items correctly`() {
        val order = Order(
            id = UUID.randomUUID(),
            customerId = UUID.randomUUID(),
            status = OrderStatus.CREATED,
            items = listOf(
                OrderItem(productId = UUID.randomUUID(), quantity = 1, price = BigDecimal("5.00")),
                OrderItem(productId = UUID.randomUUID(), quantity = 3, price = BigDecimal("15.00"))
            ),
            total = BigDecimal("50.00"),
            createdAt = Instant.now()
        )

        val entity = OrderMapper.toEntity(order)
        assertEquals(2, entity.items.size)

        val domainBack = OrderMapper.toDomain(entity)
        assertEquals(2, domainBack.items.size)
        assertEquals(order.total, domainBack.total)
    }
}

