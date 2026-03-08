package com.lobofoltran.order.infrastructure.outbox

import com.lobofoltran.order.domain.model.OutboxEvent
import com.lobofoltran.order.domain.repository.OutboxEventRepository
import io.quarkus.scheduler.Scheduled
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.jboss.logging.Logger

@ApplicationScoped
class OutboxEventPoller(
    private val outboxEventRepository: OutboxEventRepository,
    @param:Channel("order-created")
    private val emitter: Emitter<JsonObject>,
    @param:ConfigProperty(name = "outbox.poll.batch-size", defaultValue = "100")
    private val batchSize: Int,
    @param:ConfigProperty(name = "outbox.retry.max", defaultValue = "10")
    private val maxRetries: Int
) {

    private val logger = Logger.getLogger(OutboxEventPoller::class.java)

    @Scheduled(every = "{outbox.poll.interval}")
    @Transactional
    fun pollAndPublish() {
        val events = outboxEventRepository.findPendingEvents(batchSize)

        if (events.isEmpty()) return

        logger.info("Polling ${events.size} pending outbox events")

        val publishedIds = mutableListOf<java.util.UUID>()

        for (event in events) {
            if (event.retries >= maxRetries) {
                logger.warn("Max retries reached for event ${event.id} (retries=${event.retries}), skipping")
                continue
            }

            try {
                logger.info("Publishing event id=${event.id} type=${event.eventType}")
                publishEvent(event)
                publishedIds.add(event.id)
                logger.info("Published successfully event id=${event.id} for aggregate ${event.aggregateId}")
            } catch (e: Exception) {
                logger.error("Failed to publish event ${event.id}, retry scheduled (attempt ${event.retries + 1})", e)
                outboxEventRepository.incrementRetriesWithBackoff(event.id, event.retries)
            }
        }

        if (publishedIds.isNotEmpty()) {
            outboxEventRepository.markBatchAsPublished(publishedIds)
            logger.info("Marked ${publishedIds.size} events as published")
        }
    }

    private fun publishEvent(event: OutboxEvent) {
        val json = JsonObject(event.payload)
        emitter.send(json)
    }
}

