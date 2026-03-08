package com.lobofoltran.order.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "outbox_events")
class OutboxEventEntity(

    @Id
    @Column(name = "id")
    var id: UUID = UUID.randomUUID(),

    @Column(name = "aggregate_type", nullable = false, length = 100)
    var aggregateType: String = "",

    @Column(name = "aggregate_id", nullable = false)
    var aggregateId: UUID = UUID.randomUUID(),

    @Column(name = "event_type", nullable = false, length = 100)
    var eventType: String = "",

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    var payload: String = "",

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "published_at")
    var publishedAt: Instant? = null,

    @Column(name = "retries")
    var retries: Int = 0,

    @Column(name = "next_retry_at")
    var nextRetryAt: Instant? = null
)

