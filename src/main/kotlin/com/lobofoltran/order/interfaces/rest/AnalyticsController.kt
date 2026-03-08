package com.lobofoltran.order.interfaces.rest

import com.lobofoltran.order.domain.repository.OrderCustomerAnalyticsRepository
import com.lobofoltran.order.domain.repository.OrderDailyAnalyticsRepository
import com.lobofoltran.order.interfaces.dto.AnalyticsSummaryResponse
import com.lobofoltran.order.interfaces.dto.CustomerAnalyticsResponse
import com.lobofoltran.order.interfaces.dto.DailyAnalyticsResponse
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

@Path("/analytics")
@Produces(MediaType.APPLICATION_JSON)
class AnalyticsController(
    private val dailyAnalyticsRepository: OrderDailyAnalyticsRepository,
    private val customerAnalyticsRepository: OrderCustomerAnalyticsRepository
) {

    @GET
    @Path("/daily")
    fun getDailyAnalytics(@QueryParam("date") date: String?): Response {
        if (date != null) {
            val localDate = LocalDate.parse(date)
            val analytics = dailyAnalyticsRepository.findByDate(localDate)
                ?: return Response.status(Response.Status.NOT_FOUND).build()

            return Response.ok(
                DailyAnalyticsResponse(
                    date = analytics.date,
                    totalOrders = analytics.totalOrders,
                    totalRevenue = analytics.totalRevenue,
                    averageTicket = analytics.averageTicket
                )
            ).build()
        }

        val allAnalytics = dailyAnalyticsRepository.findAll()
        val response = allAnalytics.map {
            DailyAnalyticsResponse(
                date = it.date,
                totalOrders = it.totalOrders,
                totalRevenue = it.totalRevenue,
                averageTicket = it.averageTicket
            )
        }

        return Response.ok(response).build()
    }

    @GET
    @Path("/customer/{id}")
    fun getCustomerAnalytics(@PathParam("id") id: UUID): Response {
        val analytics = customerAnalyticsRepository.findByCustomerId(id)
            ?: return Response.status(Response.Status.NOT_FOUND).build()

        return Response.ok(
            CustomerAnalyticsResponse(
                customerId = analytics.customerId,
                totalOrders = analytics.totalOrders,
                totalSpent = analytics.totalSpent,
                averageTicket = analytics.averageTicket,
                lastOrderAt = analytics.lastOrderAt
            )
        ).build()
    }

    @GET
    @Path("/summary")
    fun getSummary(): Response {
        val allDaily = dailyAnalyticsRepository.findAll()

        val totalOrders = allDaily.sumOf { it.totalOrders }
        val totalRevenue = allDaily.fold(BigDecimal.ZERO) { acc, it -> acc.add(it.totalRevenue) }
        val averageTicket = if (totalOrders > 0) {
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        val dailyBreakdown = allDaily.map {
            DailyAnalyticsResponse(
                date = it.date,
                totalOrders = it.totalOrders,
                totalRevenue = it.totalRevenue,
                averageTicket = it.averageTicket
            )
        }

        return Response.ok(
            AnalyticsSummaryResponse(
                totalOrders = totalOrders,
                totalRevenue = totalRevenue,
                averageTicket = averageTicket,
                dailyBreakdown = dailyBreakdown
            )
        ).build()
    }
}

