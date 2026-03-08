package com.lobofoltran.order.infrastructure.messaging

import com.lobofoltran.order.application.service.OrderAnalyticsService
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.jboss.logging.Logger
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class OrderCreatedConsumer(
    private val analyticsService: OrderAnalyticsService
) {

    private val logger = Logger.getLogger(OrderCreatedConsumer::class.java)

    @Incoming("order-created-consumer")
    fun consume(message: JsonObject) {
        logger.info("Received OrderCreatedEvent: $message")

        val orderId = UUID.fromString(message.getString("orderId"))
        val customerId = UUID.fromString(message.getString("customerId"))
        val total = BigDecimal.valueOf(message.getDouble("total"))
        val createdAt = Instant.parse(message.getString("createdAt"))

        analyticsService.processOrder(orderId, customerId, total, createdAt)
    }
}

