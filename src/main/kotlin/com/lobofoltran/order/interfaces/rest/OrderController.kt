package com.lobofoltran.order.interfaces.rest

import com.lobofoltran.order.application.usecase.CreateOrderUseCase
import com.lobofoltran.order.domain.model.OrderItem
import com.lobofoltran.order.interfaces.dto.CreateOrderRequest
import com.lobofoltran.order.interfaces.dto.CreateOrderResponse
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase
) {

    @POST
    fun createOrder(@Valid request: CreateOrderRequest): Response {
        val items = request.items.map { item ->
            OrderItem(
                productId = item.productId!!,
                quantity = item.quantity,
                price = item.price!!
            )
        }

        val order = createOrderUseCase.execute(request.customerId!!, items)

        return Response.status(Response.Status.CREATED)
            .entity(CreateOrderResponse(orderId = order.id))
            .build()
    }
}

