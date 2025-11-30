package com.example.shopapp

import java.math.BigDecimal
import java.time.LocalDateTime

data class UserCreateRequest(
    val username: String,
    val fullName: String,
    val email: String,
    val address: String?,
    val role: UserRole = UserRole.USER
)

data class UserUpdateRequest(
    val fullName: String?,
    val email: String?,
    val address: String?,
    val role: UserRole?
)

data class UserResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val email: String,
    val address: String?,
    val role: UserRole,
    val createdAt: LocalDateTime
)

data class CategoryCreateRequest(
    val name: String,
    val description: String?
)

data class CategoryUpdateRequest(
    val name: String?,
    val description: String?
)

data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime
)

data class ProductCreateRequest(
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockCount: Int,
    val categoryId: Long
)

data class ProductUpdateRequest(
    val name: String?,
    val description: String?,
    val price: BigDecimal?,
    val stockCount: Int?,
    val categoryId: Long?
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stockCount: Int,
    val categoryId: Long,
    val categoryName: String,
    val createdAt: LocalDateTime
)

data class OrderItemCreateRequest(
    val productId: Long,
    val quantity: Int
)

data class OrderCreateRequest(
    val userId: Long,
    val items: List<OrderItemCreateRequest>,
    val paymentMethod: PaymentMethod
)

data class OrderItemResponse(
    val id: Long,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal
)

data class PaymentResponse(
    val id: Long,
    val amount: BigDecimal,
    val paymentMethod: PaymentMethod,
    val status: PaymentStatus,
    val paymentDate: LocalDateTime
)

data class OrderResponse(
    val id: Long,
    val userId: Long,
    val status: OrderStatus,
    val orderDate: LocalDateTime,
    val totalAmount: BigDecimal,
    val items: List<OrderItemResponse>,
    val payment: PaymentResponse?
)

data class UserMonthlyOrderStatsResponse(
    val userId: Long,
    val year: Int,
    val month: Int,
    val orderCount: Long,
    val totalAmount: BigDecimal
)

data class UserProductOrderStatsItem(
    val productId: Long,
    val productName: String,
    val totalQuantity: Long,
    val orderCount: Long,
    val totalAmount: BigDecimal
)

data class UserProductOrderStatsResponse(
    val userId: Long,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val items: List<UserProductOrderStatsItem>,
    val totalAmount: BigDecimal
)

data class ProductUserCountResponse(
    val productId: Long,
    val productName: String,
    val userCount: Long
)
