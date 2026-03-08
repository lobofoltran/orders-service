package com.lobofoltran.order.domain.repository

import com.lobofoltran.order.domain.model.OutboxEvent
import java.util.UUID

interface OutboxEventRepository {
    fun save(event: OutboxEvent): OutboxEvent
    fun findPendingEvents(limit: Int): List<OutboxEvent>
    fun markAsPublished(id: UUID)
    fun markBatchAsPublished(ids: List<UUID>)
    fun incrementRetriesWithBackoff(id: UUID, currentRetries: Int)
}

