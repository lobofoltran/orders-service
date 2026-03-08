package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.OrderCustomerAnalytics
import com.lobofoltran.order.infrastructure.persistence.entity.OrderCustomerAnalyticsEntity

object OrderCustomerAnalyticsMapper {

    fun toEntity(domain: OrderCustomerAnalytics): OrderCustomerAnalyticsEntity {
        return OrderCustomerAnalyticsEntity(
            customerId = domain.customerId,
            totalOrders = domain.totalOrders,
            totalSpent = domain.totalSpent,
            lastOrderAt = domain.lastOrderAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(entity: OrderCustomerAnalyticsEntity): OrderCustomerAnalytics {
        return OrderCustomerAnalytics(
            customerId = entity.customerId,
            totalOrders = entity.totalOrders,
            totalSpent = entity.totalSpent,
            lastOrderAt = entity.lastOrderAt,
            updatedAt = entity.updatedAt
        )
    }
}

