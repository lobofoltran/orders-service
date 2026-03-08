package com.lobofoltran.order.infrastructure.persistence

import com.lobofoltran.order.domain.model.OutboxEvent
import com.lobofoltran.order.domain.repository.OutboxEventRepository
import com.lobofoltran.order.infrastructure.persistence.mapper.OutboxEventMapper
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import jakarta.persistence.LockModeType
import org.hibernate.cfg.AvailableSettings
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class OutboxEventRepositoryImpl(
    private val entityManager: EntityManager
) : OutboxEventRepository {

    override fun save(event: OutboxEvent): OutboxEvent {
        val entity = OutboxEventMapper.toEntity(event)
        entityManager.persist(entity)
        return OutboxEventMapper.toDomain(entity)
    }

    override fun findPendingEvents(limit: Int): List<OutboxEvent> {
        val now = Instant.now()

        val results = entityManager.createQuery(
            """
            SELECT e FROM OutboxEventEntity e
            WHERE e.publishedAt IS NULL
              AND e.retries < 10
              AND (e.nextRetryAt IS NULL OR e.nextRetryAt <= :now)
            ORDER BY e.createdAt
            """.trimIndent(),
            com.lobofoltran.order.infrastructure.persistence.entity.OutboxEventEntity::class.java
        )
            .setParameter("now", now)
            .setMaxResults(limit)
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .setHint(AvailableSettings.JAKARTA_LOCK_TIMEOUT, -2)
            .resultList

        return results.map { OutboxEventMapper.toDomain(it) }
    }

    override fun markAsPublished(id: UUID) {
        val entity = entityManager.find(
            com.lobofoltran.order.infrastructure.persistence.entity.OutboxEventEntity::class.java,
            id
        ) ?: return

        entity.publishedAt = Instant.now()
    }

    override fun markBatchAsPublished(ids: List<UUID>) {
        if (ids.isEmpty()) return

        entityManager.createQuery(
            """
            UPDATE OutboxEventEntity e
            SET e.publishedAt = :now
            WHERE e.id IN :ids
            """.trimIndent()
        )
            .setParameter("now", Instant.now())
            .setParameter("ids", ids)
            .executeUpdate()
    }

    override fun incrementRetriesWithBackoff(id: UUID, currentRetries: Int) {
        val entity = entityManager.find(
            com.lobofoltran.order.infrastructure.persistence.entity.OutboxEventEntity::class.java,
            id
        ) ?: return

        entity.retries = currentRetries + 1
        entity.nextRetryAt = Instant.now().plusSeconds((currentRetries + 1) * 5L)
    }
}

