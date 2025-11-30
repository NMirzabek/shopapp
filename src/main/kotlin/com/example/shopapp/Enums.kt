package com.example.shopapp

enum class UserRole {
    ADMIN, USER
}

enum class OrderStatus {
    PENDING, DELIVERED, FINISHED, CANCELLED
}

enum class PaymentStatus {
    PENDING, PAID, FAILED
}

enum class PaymentMethod {
    CASH, CARD
}
