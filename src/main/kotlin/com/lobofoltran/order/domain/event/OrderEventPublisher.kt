package com.lobofoltran.order.domain.event

interface OrderEventPublisher {
    fun publish(event: OrderCreatedEvent)
}

