package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.Order
import com.lobofoltran.order.domain.model.OrderItem
import com.lobofoltran.order.domain.model.OrderStatus
import com.lobofoltran.order.infrastructure.persistence.entity.OrderEntity
import com.lobofoltran.order.infrastructure.persistence.entity.OrderItemEntity

object OrderMapper {

    fun toEntity(order: Order): OrderEntity {
        val entity = OrderEntity(
            id = order.id,
            customerId = order.customerId,
            status = order.status,
            total = order.total,
            createdAt = order.createdAt
        )
        val itemEntities = order.items.map { item ->
            OrderItemEntity(
                productId = item.productId,
                quantity = item.quantity,
                price = item.price,
                order = entity
            )
        }.toMutableList()
        entity.items = itemEntities
        return entity
    }

    fun toDomain(entity: OrderEntity): Order {
        val items = entity.items.map { itemEntity ->
            OrderItem(
                productId = itemEntity.productId,
                quantity = itemEntity.quantity,
                price = itemEntity.price
            )
        }
        return Order(
            id = entity.id,
            customerId = entity.customerId,
            status = entity.status,
            items = items,
            total = entity.total,
            createdAt = entity.createdAt
        )
    }
}

