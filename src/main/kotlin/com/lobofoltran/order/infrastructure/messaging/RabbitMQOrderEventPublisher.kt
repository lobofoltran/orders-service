package com.lobofoltran.order.infrastructure.messaging

import com.lobofoltran.order.domain.event.OrderCreatedEvent
import com.lobofoltran.order.domain.event.OrderEventPublisher
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.jboss.logging.Logger

@ApplicationScoped
class RabbitMQOrderEventPublisher(
    @param:Channel("order-created")
    private val emitter: Emitter<JsonObject>
) : OrderEventPublisher {

    private val logger = Logger.getLogger(RabbitMQOrderEventPublisher::class.java)

    override fun publish(event: OrderCreatedEvent) {

        val json = JsonObject()
            .put("orderId", event.orderId.toString())
            .put("customerId", event.customerId.toString())
            .put("total", event.total.toDouble())
            .put("createdAt", event.createdAt.toString())

        emitter.send(json)

        logger.info("Published OrderCreatedEvent for order ${event.orderId}")
    }
}