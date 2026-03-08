package com.lobofoltran.order.domain.repository

import java.util.UUID

interface ProcessedAnalyticsEventRepository {
    fun existsByEventId(eventId: UUID): Boolean
    fun save(eventId: UUID)
}

