package com.lobofoltran.order.infrastructure.persistence

import com.lobofoltran.order.domain.model.Order
import com.lobofoltran.order.domain.repository.OrderRepository
import com.lobofoltran.order.infrastructure.persistence.mapper.OrderMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager

@ApplicationScoped
class OrderRepositoryImpl(
    private val entityManager: EntityManager
) : OrderRepository {

    override fun save(order: Order): Order {
        val entity = OrderMapper.toEntity(order)
        entityManager.persist(entity)
        entityManager.flush()
        return OrderMapper.toDomain(entity)
    }
}

