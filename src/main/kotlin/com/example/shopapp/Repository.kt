package com.example.shopapp

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDateTime

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}

interface CategoryRepository : JpaRepository<Category, Long>

interface ProductRepository : JpaRepository<Product, Long>

interface OrderRepository : JpaRepository<Order, Long> {

    fun findAllByUserId(userId: Long): List<Order>

    fun findAllByUserIdAndStatus(userId: Long, status: OrderStatus): List<Order>

    fun findAllByUserIdAndOrderDateBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Order>

    @Query(
        """
        select coalesce(sum(o.totalAmount), 0)
        from Order o
        where o.user.id = :userId
          and o.orderDate between :from and :to
    """
    )
    fun sumTotalAmountByUserAndPeriod(
        @Param("userId") userId: Long,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): BigDecimal

    fun countByUserIdAndOrderDateBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): Long
}

interface OrderItemRepository : JpaRepository<OrderItem, Long> {

    fun findAllByOrderUserIdAndOrderOrderDateBetween(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<OrderItem>

    @Query(
        """
        select count(distinct o.user.id)
        from OrderItem oi
        join oi.order o
        where oi.product.id = :productId
    """
    )
    fun countDistinctUsersByProductId(@Param("productId") productId: Long): Long
}

interface PaymentRepository : JpaRepository<Payment, Long> {

    fun findAllByUserId(userId: Long): List<Payment>

    fun findByOrderId(orderId: Long): Payment?
}
