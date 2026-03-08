package com.lobofoltran.order.interfaces.dto

import java.math.BigDecimal

data class AnalyticsSummaryResponse(
    val totalOrders: Long,
    val totalRevenue: BigDecimal,
    val averageTicket: BigDecimal,
    val dailyBreakdown: List<DailyAnalyticsResponse>
)

