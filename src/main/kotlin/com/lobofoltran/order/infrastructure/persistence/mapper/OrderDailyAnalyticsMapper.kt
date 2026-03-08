package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.OrderDailyAnalytics
import com.lobofoltran.order.infrastructure.persistence.entity.OrderDailyAnalyticsEntity

object OrderDailyAnalyticsMapper {

    fun toEntity(domain: OrderDailyAnalytics): OrderDailyAnalyticsEntity {
        return OrderDailyAnalyticsEntity(
            date = domain.date,
            totalOrders = domain.totalOrders,
            totalRevenue = domain.totalRevenue,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(entity: OrderDailyAnalyticsEntity): OrderDailyAnalytics {
        return OrderDailyAnalytics(
            date = entity.date,
            totalOrders = entity.totalOrders,
            totalRevenue = entity.totalRevenue,
            updatedAt = entity.updatedAt
        )
    }
}

