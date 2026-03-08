package com.lobofoltran.order.infrastructure.persistence

import com.lobofoltran.order.domain.repository.ProcessedAnalyticsEventRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class ProcessedAnalyticsEventRepositoryImpl(
    private val entityManager: EntityManager
) : ProcessedAnalyticsEventRepository {

    override fun existsByEventId(eventId: UUID): Boolean {
        val count = entityManager.createNativeQuery(
            "SELECT COUNT(*) FROM processed_analytics_events WHERE event_id = :eventId"
        )
            .setParameter("eventId", eventId)
            .singleResult as Number

        return count.toLong() > 0
    }

    override fun save(eventId: UUID) {
        entityManager.createNativeQuery(
            """
            INSERT INTO processed_analytics_events (event_id, processed_at)
            VALUES (:eventId, :processedAt)
            ON CONFLICT (event_id) DO NOTHING
            """.trimIndent()
        )
            .setParameter("eventId", eventId)
            .setParameter("processedAt", Instant.now())
            .executeUpdate()
    }
}

