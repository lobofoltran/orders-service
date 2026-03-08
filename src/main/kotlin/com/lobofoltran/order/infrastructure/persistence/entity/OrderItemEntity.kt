package com.lobofoltran.order.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "order_items")
class OrderItemEntity(

    @Id
    @Column(name = "id")
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: OrderEntity? = null,

    @Column(name = "product_id", nullable = false)
    var productId: UUID = UUID.randomUUID(),

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 0,

    @Column(name = "price", nullable = false)
    var price: BigDecimal = BigDecimal.ZERO
)

