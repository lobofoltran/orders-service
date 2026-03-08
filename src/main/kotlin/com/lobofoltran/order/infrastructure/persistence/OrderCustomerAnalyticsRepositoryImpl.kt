package com.lobofoltran.order.infrastructure.persistence

import com.lobofoltran.order.domain.model.OrderCustomerAnalytics
import com.lobofoltran.order.domain.repository.OrderCustomerAnalyticsRepository
import com.lobofoltran.order.infrastructure.persistence.entity.OrderCustomerAnalyticsEntity
import com.lobofoltran.order.infrastructure.persistence.mapper.OrderCustomerAnalyticsMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class OrderCustomerAnalyticsRepositoryImpl(
    private val entityManager: EntityManager
) : OrderCustomerAnalyticsRepository {

    override fun findByCustomerId(customerId: UUID): OrderCustomerAnalytics? {
        val entity = entityManager.find(OrderCustomerAnalyticsEntity::class.java, customerId)
        return entity?.let { OrderCustomerAnalyticsMapper.toDomain(it) }
    }

    override fun upsert(customerId: UUID, revenue: BigDecimal, orderAt: Instant, updatedAt: Instant) {
        entityManager.createNativeQuery(
            """
            INSERT INTO order_customer_analytics (customer_id, total_orders, total_spent, last_order_at, updated_at)
            VALUES (:customerId, 1, :revenue, :orderAt, :updatedAt)
            ON CONFLICT (customer_id) DO UPDATE SET
                total_orders = order_customer_analytics.total_orders + 1,
                total_spent = order_customer_analytics.total_spent + :revenue,
                last_order_at = GREATEST(order_customer_analytics.last_order_at, :orderAt),
                updated_at = :updatedAt
            """.trimIndent()
        )
            .setParameter("customerId", customerId)
            .setParameter("revenue", revenue)
            .setParameter("orderAt", orderAt)
            .setParameter("updatedAt", updatedAt)
            .executeUpdate()
    }
}

