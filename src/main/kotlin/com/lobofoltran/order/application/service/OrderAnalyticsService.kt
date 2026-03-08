package com.lobofoltran.order.application.service

import com.lobofoltran.order.domain.repository.OrderCustomerAnalyticsRepository
import com.lobofoltran.order.domain.repository.OrderDailyAnalyticsRepository
import com.lobofoltran.order.domain.repository.ProcessedAnalyticsEventRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.jboss.logging.Logger
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

@ApplicationScoped
class OrderAnalyticsService(
    private val dailyRepository: OrderDailyAnalyticsRepository,
    private val customerRepository: OrderCustomerAnalyticsRepository,
    private val processedEventRepository: ProcessedAnalyticsEventRepository
) {

    private val logger = Logger.getLogger(OrderAnalyticsService::class.java)

    @Transactional
    fun processOrder(
        orderId: UUID,
        customerId: UUID,
        total: BigDecimal,
        createdAt: Instant
    ) {
        if (processedEventRepository.existsByEventId(orderId)) {
            logger.info("Event for order $orderId already processed, skipping")
            return
        }

        logger.info("Processing analytics for order $orderId")

        val now = Instant.now()

        updateDailyAnalytics(total, createdAt, now)
        updateCustomerAnalytics(customerId, total, createdAt, now)

        processedEventRepository.save(orderId)

        logger.info("Analytics updated for order $orderId")
    }

    private fun updateDailyAnalytics(total: BigDecimal, createdAt: Instant, now: Instant) {
        val date = createdAt.atZone(ZoneOffset.UTC).toLocalDate()
        dailyRepository.upsert(date, total, now)
        logger.info("Daily analytics updated for date $date")
    }

    private fun updateCustomerAnalytics(customerId: UUID, total: BigDecimal, createdAt: Instant, now: Instant) {
        customerRepository.upsert(customerId, total, createdAt, now)
        logger.info("Customer analytics updated for customer $customerId")
    }
}