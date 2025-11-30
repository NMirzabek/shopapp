package com.example.shopapp

import org.springframework.stereotype.Component

@Component
class Mapper {

    fun toUserResponse(user: User) = UserResponse(
        id = user.id!!,
        username = user.username,
        fullName = user.fullName,
        email = user.email,
        address = user.address,
        role = user.role,
        createdAt = user.createdAt
    )

    fun toCategoryResponse(category: Category) = CategoryResponse(
        id = category.id!!,
        name = category.name,
        description = category.description,
        createdAt = category.createdAt
    )

    fun toProductResponse(product: Product) = ProductResponse(
        id = product.id!!,
        name = product.name,
        description = product.description,
        price = product.price,
        stockCount = product.stockCount,
        categoryId = product.category.id!!,
        categoryName = product.category.name,
        createdAt = product.createdAt
    )

    fun toOrderItemResponse(item: OrderItem) = OrderItemResponse(
        id = item.id!!,
        productId = item.product.id!!,
        productName = item.product.name,
        quantity = item.quantity,
        unitPrice = item.unitPrice,
        totalPrice = item.totalPrice
    )

    fun toPaymentResponse(payment: Payment) = PaymentResponse(
        id = payment.id!!,
        amount = payment.amount,
        paymentMethod = payment.paymentMethod,
        status = payment.status,
        paymentDate = payment.paymentDate
    )

    fun toOrderResponse(order: Order, payment: Payment?) = OrderResponse(
        id = order.id!!,
        userId = order.user.id!!,
        status = order.status,
        orderDate = order.orderDate,
        totalAmount = order.totalAmount,
        items = order.items.map { toOrderItemResponse(it) },
        payment = payment?.let { toPaymentResponse(it) }
    )
}
