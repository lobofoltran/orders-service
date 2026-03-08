package com.lobofoltran.order.interfaces.dto

import java.math.BigDecimal
import java.time.LocalDate

data class DailyAnalyticsResponse(
    val date: LocalDate,
    val totalOrders: Long,
    val totalRevenue: BigDecimal,
    val averageTicket: BigDecimal
)

