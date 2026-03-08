package com.lobofoltran.order.domain.model

import java.time.Instant
import java.util.UUID

data class OutboxEvent(
    val id: UUID,
    val aggregateType: String,
    val aggregateId: UUID,
    val eventType: String,
    val payload: String,
    val createdAt: Instant,
    val publishedAt: Instant?,
    val retries: Int,
    val nextRetryAt: Instant?
) {
    companion object {
        fun create(
            aggregateType: String,
            aggregateId: UUID,
            eventType: String,
            payload: String
        ): OutboxEvent {
            return OutboxEvent(
                id = UUID.randomUUID(),
                aggregateType = aggregateType,
                aggregateId = aggregateId,
                eventType = eventType,
                payload = payload,
                createdAt = Instant.now(),
                publishedAt = null,
                retries = 0,
                nextRetryAt = null
            )
        }
    }
}

