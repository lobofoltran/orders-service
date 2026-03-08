package com.lobofoltran.order.domain.repository

import com.lobofoltran.order.domain.model.Order

interface OrderRepository {
    fun save(order: Order): Order
}

