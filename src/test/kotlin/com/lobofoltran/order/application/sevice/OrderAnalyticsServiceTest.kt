package com.lobofoltran.order.application.sevice

import com.lobofoltran.order.application.service.OrderAnalyticsService
import com.lobofoltran.order.domain.repository.OrderCustomerAnalyticsRepository
import com.lobofoltran.order.domain.repository.OrderDailyAnalyticsRepository
import com.lobofoltran.order.domain.repository.ProcessedAnalyticsEventRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.UUID

class OrderAnalyticsServiceTest {

    private val dailyRepository: OrderDailyAnalyticsRepository = mock()
    private val customerRepository: OrderCustomerAnalyticsRepository = mock()
    private val processedEventRepository: ProcessedAnalyticsEventRepository = mock()

    private val service = OrderAnalyticsService(
        dailyRepository,
        customerRepository,
        processedEventRepository
    )

    private val orderId: UUID = UUID.randomUUID()
    private val customerId: UUID = UUID.randomUUID()
    private val total: BigDecimal = BigDecimal("150.00")
    private val createdAt: Instant = Instant.parse("2026-03-08T10:00:00Z")

    @Test
    fun `should process order and update daily and customer analytics`() {
        whenever(processedEventRepository.existsByEventId(orderId)).thenReturn(false)

        service.processOrder(orderId, customerId, total, createdAt)

        val expectedDate = createdAt.atZone(ZoneOffset.UTC).toLocalDate()

        verify(processedEventRepository).existsByEventId(orderId)
        verify(dailyRepository).upsert(
            any<LocalDate>(),
            any<BigDecimal>(),
            any<Instant>()
        )
        verify(customerRepository).upsert(
            any<UUID>(),
            any<BigDecimal>(),
            any<Instant>(),
            any<Instant>()
        )
        verify(processedEventRepository).save(orderId)
    }

    @Test
    fun `should skip processing when event already processed (idempotency)`() {
        whenever(processedEventRepository.existsByEventId(orderId)).thenReturn(true)

        service.processOrder(orderId, customerId, total, createdAt)

        verify(processedEventRepository).existsByEventId(orderId)
        verify(dailyRepository, never()).upsert(any(), any(), any())
        verify(customerRepository, never()).upsert(any(), any(), any(), any())
        verify(processedEventRepository, never()).save(any())
    }

    @Test
    fun `should use UTC date from createdAt for daily analytics`() {
        whenever(processedEventRepository.existsByEventId(orderId)).thenReturn(false)

        val midnight = Instant.parse("2026-03-08T23:59:59Z")
        service.processOrder(orderId, customerId, total, midnight)

        val expectedDate = LocalDate.of(2026, 3, 8)
        verify(dailyRepository).upsert(
            eq(expectedDate),
            eq(total),
            any()
        )
    }

    @Test
    fun `should pass correct customer id and total to customer repository`() {
        whenever(processedEventRepository.existsByEventId(orderId)).thenReturn(false)

        service.processOrder(orderId, customerId, total, createdAt)

        verify(customerRepository).upsert(
            eq(customerId),
            eq(total),
            eq(createdAt),
            any()
        )
    }

    @Test
    fun `should save processed event after updating analytics`() {
        whenever(processedEventRepository.existsByEventId(orderId)).thenReturn(false)

        service.processOrder(orderId, customerId, total, createdAt)

        verify(processedEventRepository).save(orderId)
    }
}