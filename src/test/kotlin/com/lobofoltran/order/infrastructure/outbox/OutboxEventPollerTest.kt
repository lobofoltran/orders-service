package com.lobofoltran.order.infrastructure.outbox

import com.lobofoltran.order.domain.model.OutboxEvent
import com.lobofoltran.order.domain.repository.OutboxEventRepository
import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.UUID

class OutboxEventPollerTest {

    private val outboxEventRepository: OutboxEventRepository = mock()
    private val emitter: Emitter<JsonObject> = mock()

    private fun createPoller(batchSize: Int = 100, maxRetries: Int = 10): OutboxEventPoller {
        return OutboxEventPoller(
            outboxEventRepository = outboxEventRepository,
            emitter = emitter,
            batchSize = batchSize,
            maxRetries = maxRetries
        )
    }

    private fun createPendingEvent(retries: Int = 0, nextRetryAt: Instant? = null): OutboxEvent {
        return OutboxEvent(
            id = UUID.randomUUID(),
            aggregateType = "Order",
            aggregateId = UUID.randomUUID(),
            eventType = "OrderCreatedEvent",
            payload = """{"orderId":"${UUID.randomUUID()}","customerId":"${UUID.randomUUID()}","total":100.0,"createdAt":"${Instant.now()}"}""",
            createdAt = Instant.now(),
            publishedAt = null,
            retries = retries,
            nextRetryAt = nextRetryAt
        )
    }

    @Test
    fun `should do nothing when no pending events`() {
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(emptyList())

        val poller = createPoller()
        poller.pollAndPublish()

        verify(emitter, never()).send(any<JsonObject>())
        verify(outboxEventRepository, never()).markBatchAsPublished(any())
    }

    @Test
    fun `should publish pending events and mark batch as published`() {
        val event = createPendingEvent()
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(listOf(event))

        val poller = createPoller()
        poller.pollAndPublish()

        verify(emitter).send(any<JsonObject>())
        verify(outboxEventRepository).markBatchAsPublished(listOf(event.id))
    }

    @Test
    fun `should publish multiple events and batch mark as published`() {
        val event1 = createPendingEvent()
        val event2 = createPendingEvent()
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(listOf(event1, event2))

        val poller = createPoller()
        poller.pollAndPublish()

        verify(emitter, times(2)).send(any<JsonObject>())
        verify(outboxEventRepository).markBatchAsPublished(listOf(event1.id, event2.id))
    }

    @Test
    fun `should increment retries with backoff on publish failure`() {
        val event = createPendingEvent(retries = 2)
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(listOf(event))
        doThrow(RuntimeException("RabbitMQ down")).whenever(emitter).send(any<JsonObject>())

        val poller = createPoller()
        poller.pollAndPublish()

        verify(outboxEventRepository).incrementRetriesWithBackoff(event.id, 2)
        verify(outboxEventRepository, never()).markBatchAsPublished(any())
    }

    @Test
    fun `should skip events that exceeded max retries`() {
        val event = createPendingEvent(retries = 10)
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(listOf(event))

        val poller = createPoller()
        poller.pollAndPublish()

        verify(emitter, never()).send(any<JsonObject>())
        verify(outboxEventRepository, never()).markBatchAsPublished(any())
        verify(outboxEventRepository, never()).incrementRetriesWithBackoff(any(), any())
    }

    @Test
    fun `should continue processing after one event fails`() {
        val event1 = createPendingEvent()
        val event2 = createPendingEvent()
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(listOf(event1, event2))

        var callCount = 0
        whenever(emitter.send(any<JsonObject>())).thenAnswer {
            callCount++
            if (callCount == 1) throw RuntimeException("RabbitMQ down")
            null
        }

        val poller = createPoller()
        poller.pollAndPublish()

        verify(outboxEventRepository).incrementRetriesWithBackoff(event1.id, 0)
        verify(outboxEventRepository).markBatchAsPublished(listOf(event2.id))
    }

    @Test
    fun `should use configured batch size`() {
        whenever(outboxEventRepository.findPendingEvents(50)).thenReturn(emptyList())

        val poller = createPoller(batchSize = 50)
        poller.pollAndPublish()

        verify(outboxEventRepository).findPendingEvents(50)
    }

    @Test
    fun `should pass current retries to incrementRetriesWithBackoff`() {
        val event = createPendingEvent(retries = 5)
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(listOf(event))
        doThrow(RuntimeException("fail")).whenever(emitter).send(any<JsonObject>())

        val poller = createPoller()
        poller.pollAndPublish()

        verify(outboxEventRepository).incrementRetriesWithBackoff(eq(event.id), eq(5))
    }

    @Test
    fun `should not call markBatchAsPublished when all events fail`() {
        val event1 = createPendingEvent()
        val event2 = createPendingEvent()
        whenever(outboxEventRepository.findPendingEvents(100)).thenReturn(listOf(event1, event2))
        doThrow(RuntimeException("fail")).whenever(emitter).send(any<JsonObject>())

        val poller = createPoller()
        poller.pollAndPublish()

        verify(outboxEventRepository, never()).markBatchAsPublished(any())
        verify(outboxEventRepository, times(2)).incrementRetriesWithBackoff(any(), any())
    }
}

