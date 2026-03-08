package com.lobofoltran.order.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "order_daily_analytics")
class OrderDailyAnalyticsEntity(

    @Id
    @Column(name = "date")
    var date: LocalDate = LocalDate.now(),

    @Column(name = "total_orders", nullable = false)
    var totalOrders: Long = 0,

    @Column(name = "total_revenue", nullable = false, precision = 18, scale = 2)
    var totalRevenue: BigDecimal = BigDecimal.ZERO,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)

