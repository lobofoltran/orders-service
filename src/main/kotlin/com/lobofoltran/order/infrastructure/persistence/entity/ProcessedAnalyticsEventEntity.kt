package com.lobofoltran.order.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "processed_analytics_events")
class ProcessedAnalyticsEventEntity(

    @Id
    @Column(name = "event_id")
    var eventId: UUID = UUID.randomUUID(),

    @Column(name = "processed_at", nullable = false)
    var processedAt: Instant = Instant.now()
)

