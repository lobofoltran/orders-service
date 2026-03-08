package com.lobofoltran.order.infrastructure.persistence.mapper

import com.lobofoltran.order.domain.model.OutboxEvent
import com.lobofoltran.order.infrastructure.persistence.entity.OutboxEventEntity

object OutboxEventMapper {

    fun toEntity(event: OutboxEvent): OutboxEventEntity {
        return OutboxEventEntity(
            id = event.id,
            aggregateType = event.aggregateType,
            aggregateId = event.aggregateId,
            eventType = event.eventType,
            payload = event.payload,
            createdAt = event.createdAt,
            publishedAt = event.publishedAt,
            retries = event.retries,
            nextRetryAt = event.nextRetryAt
        )
    }

    fun toDomain(entity: OutboxEventEntity): OutboxEvent {
        return OutboxEvent(
            id = entity.id,
            aggregateType = entity.aggregateType,
            aggregateId = entity.aggregateId,
            eventType = entity.eventType,
            payload = entity.payload,
            createdAt = entity.createdAt,
            publishedAt = entity.publishedAt,
            retries = entity.retries,
            nextRetryAt = entity.nextRetryAt
        )
    }
}

