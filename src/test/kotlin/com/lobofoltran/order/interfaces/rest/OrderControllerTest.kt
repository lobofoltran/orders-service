package com.lobofoltran.order.interfaces.rest

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import java.util.UUID

@QuarkusTest
class OrderControllerTest {

    @Test
    fun `should create order successfully`() {
        val customerId = UUID.randomUUID()
        val productId = UUID.randomUUID()

        val body = """
            {
                "customerId": "$customerId",
                "items": [
                    {
                        "productId": "$productId",
                        "quantity": 2,
                        "price": 10.00
                    }
                ]
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/orders")
            .then()
            .statusCode(201)
            .body("orderId", notNullValue())
    }

    @Test
    fun `should create order with multiple items`() {
        val customerId = UUID.randomUUID()

        val body = """
            {
                "customerId": "$customerId",
                "items": [
                    {
                        "productId": "${UUID.randomUUID()}",
                        "quantity": 1,
                        "price": 25.00
                    },
                    {
                        "productId": "${UUID.randomUUID()}",
                        "quantity": 3,
                        "price": 10.00
                    }
                ]
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/orders")
            .then()
            .statusCode(201)
            .body("orderId", notNullValue())
    }

    @Test
    fun `should return 400 when customerId is missing`() {
        val body = """
            {
                "items": [
                    {
                        "productId": "${UUID.randomUUID()}",
                        "quantity": 2,
                        "price": 10.00
                    }
                ]
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/orders")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should return 400 when items is empty`() {
        val body = """
            {
                "customerId": "${UUID.randomUUID()}",
                "items": []
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/orders")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should return 400 when quantity is zero`() {
        val body = """
            {
                "customerId": "${UUID.randomUUID()}",
                "items": [
                    {
                        "productId": "${UUID.randomUUID()}",
                        "quantity": 0,
                        "price": 10.00
                    }
                ]
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/orders")
            .then()
            .statusCode(400)
    }

    @Test
    fun `should return 400 when price is zero`() {
        val body = """
            {
                "customerId": "${UUID.randomUUID()}",
                "items": [
                    {
                        "productId": "${UUID.randomUUID()}",
                        "quantity": 1,
                        "price": 0
                    }
                ]
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/orders")
            .then()
            .statusCode(400)
    }
}

