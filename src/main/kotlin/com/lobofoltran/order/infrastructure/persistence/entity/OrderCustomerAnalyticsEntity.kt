package com.lobofoltran.order.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "order_customer_analytics")
class OrderCustomerAnalyticsEntity(

    @Id
    @Column(name = "customer_id")
    var customerId: UUID = UUID.randomUUID(),

    @Column(name = "total_orders", nullable = false)
    var totalOrders: Long = 0,

    @Column(name = "total_spent", nullable = false, precision = 18, scale = 2)
    var totalSpent: BigDecimal = BigDecimal.ZERO,

    @Column(name = "last_order_at")
    var lastOrderAt: Instant? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)

