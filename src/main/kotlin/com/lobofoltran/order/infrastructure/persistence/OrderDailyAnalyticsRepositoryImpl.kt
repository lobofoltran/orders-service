package com.lobofoltran.order.infrastructure.persistence

import com.lobofoltran.order.domain.model.OrderDailyAnalytics
import com.lobofoltran.order.domain.repository.OrderDailyAnalyticsRepository
import com.lobofoltran.order.infrastructure.persistence.entity.OrderDailyAnalyticsEntity
import com.lobofoltran.order.infrastructure.persistence.mapper.OrderDailyAnalyticsMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@ApplicationScoped
class OrderDailyAnalyticsRepositoryImpl(
    private val entityManager: EntityManager
) : OrderDailyAnalyticsRepository {

    override fun findByDate(date: LocalDate): OrderDailyAnalytics? {
        val entity = entityManager.find(OrderDailyAnalyticsEntity::class.java, date)
        return entity?.let { OrderDailyAnalyticsMapper.toDomain(it) }
    }

    override fun upsert(date: LocalDate, revenue: BigDecimal, updatedAt: Instant) {
        entityManager.createNativeQuery(
            """
            INSERT INTO order_daily_analytics (date, total_orders, total_revenue, updated_at)
            VALUES (:date, 1, :revenue, :updatedAt)
            ON CONFLICT (date) DO UPDATE SET
                total_orders = order_daily_analytics.total_orders + 1,
                total_revenue = order_daily_analytics.total_revenue + :revenue,
                updated_at = :updatedAt
            """.trimIndent()
        )
            .setParameter("date", date)
            .setParameter("revenue", revenue)
            .setParameter("updatedAt", updatedAt)
            .executeUpdate()
    }

    @Suppress("UNCHECKED_CAST")
    override fun findAll(): List<OrderDailyAnalytics> {
        val results = entityManager.createNativeQuery(
            "SELECT * FROM order_daily_analytics ORDER BY date DESC",
            OrderDailyAnalyticsEntity::class.java
        ).resultList as List<OrderDailyAnalyticsEntity>

        return results.map { OrderDailyAnalyticsMapper.toDomain(it) }
    }
}

