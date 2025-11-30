package com.example.shopapp

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Service
class UserService(
    private val userRepository: UserRepository,
    private val mapper: Mapper
) {

    fun create(req: UserCreateRequest): UserResponse {
        if (userRepository.findByUsername(req.username) != null) {
            throw BadRequestException("Username already exists")
        }
        val user = User(
            username = req.username,
            fullName = req.fullName,
            email = req.email,
            address = req.address,
            role = req.role
        )
        return mapper.toUserResponse(userRepository.save(user))
    }

    fun get(id: Long): UserResponse =
        mapper.toUserResponse(
            userRepository.findById(id)
                .orElseThrow { NotFoundException("User not found") }
        )

    fun getAll(): List<UserResponse> =
        userRepository.findAll().map { mapper.toUserResponse(it) }

    fun update(id: Long, req: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User not found") }

        req.fullName?.let { user.fullName = it }
        req.email?.let { user.email = it }
        req.address?.let { user.address = it }
        req.role?.let { user.role = it }

        return mapper.toUserResponse(userRepository.save(user))
    }

    fun delete(id: Long) {
        if (!userRepository.existsById(id)) throw NotFoundException("User not found")
        userRepository.deleteById(id)
    }
}

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val mapper: Mapper
) {

    fun create(req: CategoryCreateRequest): CategoryResponse {
        val category = Category(
            name = req.name,
            description = req.description
        )
        return mapper.toCategoryResponse(categoryRepository.save(category))
    }

    fun get(id: Long): CategoryResponse =
        mapper.toCategoryResponse(
            categoryRepository.findById(id)
                .orElseThrow { NotFoundException("Category not found") }
        )

    fun getAll(): List<CategoryResponse> =
        categoryRepository.findAll().map { mapper.toCategoryResponse(it) }

    fun update(id: Long, req: CategoryUpdateRequest): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { NotFoundException("Category not found") }

        req.name?.let { category.name = it }
        req.description?.let { category.description = it }

        return mapper.toCategoryResponse(categoryRepository.save(category))
    }

    fun delete(id: Long) {
        if (!categoryRepository.existsById(id)) throw NotFoundException("Category not found")
        categoryRepository.deleteById(id)
    }
}

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val mapper: Mapper
) {

    fun create(req: ProductCreateRequest): ProductResponse {
        val category = categoryRepository.findById(req.categoryId)
            .orElseThrow { NotFoundException("Category not found") }

        val product = Product(
            name = req.name,
            description = req.description,
            price = req.price,
            stockCount = req.stockCount,
            category = category
        )
        return mapper.toProductResponse(productRepository.save(product))
    }

    fun get(id: Long): ProductResponse =
        mapper.toProductResponse(
            productRepository.findById(id)
                .orElseThrow { NotFoundException("Product not found") }
        )

    fun getAll(): List<ProductResponse> =
        productRepository.findAll().map { mapper.toProductResponse(it) }

    fun update(id: Long, req: ProductUpdateRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Product not found") }

        req.name?.let { product.name = it }
        req.description?.let { product.description = it }
        req.price?.let { product.price = it }
        req.stockCount?.let { product.stockCount = it }

        if (req.categoryId != null) {
            val category = categoryRepository.findById(req.categoryId)
                .orElseThrow { NotFoundException("Category not found") }
            product.category = category
        }

        return mapper.toProductResponse(productRepository.save(product))
    }

    fun delete(id: Long) {
        if (!productRepository.existsById(id)) throw NotFoundException("Product not found")
        productRepository.deleteById(id)
    }
}

@Service
class OrderService(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val paymentRepository: PaymentRepository,
    private val mapper: Mapper
) {


    @Transactional
    fun createOrder(req: OrderCreateRequest): OrderResponse {
        val user = userRepository.findById(req.userId)
            .orElseThrow { NotFoundException("User not found") }

        if (req.items.isEmpty()) {
            throw BadRequestException("Order must contain at least one item")
        }

        val order = Order(user = user)
        val items = mutableListOf<OrderItem>()
        var totalAmount = BigDecimal.ZERO

        req.items.forEach { itemReq ->
            val product = productRepository.findById(itemReq.productId)
                .orElseThrow { NotFoundException("Product not found: ${itemReq.productId}") }

            if (itemReq.quantity <= 0) {
                throw BadRequestException("Quantity must be > 0")
            }

            if (product.stockCount < itemReq.quantity) {
                throw BadRequestException("Not enough stock for product ${product.name}")
            }

            val unitPrice = product.price
            val totalPrice = unitPrice.multiply(BigDecimal(itemReq.quantity))

            product.stockCount -= itemReq.quantity

            val orderItem = OrderItem(
                order = order,
                product = product,
                quantity = itemReq.quantity,
                unitPrice = unitPrice,
                totalPrice = totalPrice
            )
            items.add(orderItem)
            totalAmount = totalAmount.add(totalPrice)
        }

        order.totalAmount = totalAmount
        order.items = items

        val savedOrder = orderRepository.save(order)

        val payment = Payment(
            order = savedOrder,
            user = user,
            paymentMethod = req.paymentMethod,
            amount = totalAmount,
            status = PaymentStatus.PAID
        )

        val savedPayment = paymentRepository.save(payment)

        return mapper.toOrderResponse(savedOrder, savedPayment)
    }


    fun getOrder(id: Long): OrderResponse {
        val order = orderRepository.findById(id)
            .orElseThrow { NotFoundException("Order not found") }

        val payment = paymentRepository.findByOrderId(order.id!!)
        return mapper.toOrderResponse(order, payment)
    }

    fun getUserOrders(userId: Long): List<OrderResponse> {
        val orders = orderRepository.findAllByUserId(userId)
        return orders.map { o ->
            val pay = paymentRepository.findByOrderId(o.id!!)
            mapper.toOrderResponse(o, pay)
        }
    }


    @Transactional
    fun cancelOrderByUser(orderId: Long, userId: Long) {
        val order = orderRepository.findById(orderId)
            .orElseThrow { NotFoundException("Order not found") }

        if (order.user.id != userId) {
            throw BadRequestException("You can cancel only your own orders")
        }

        if (order.status != OrderStatus.PENDING) {
            throw BadRequestException("Only PENDING orders can be cancelled")
        }

        order.status = OrderStatus.CANCELLED
    }


    @Transactional
    fun changeStatusByAdmin(orderId: Long, newStatus: OrderStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            throw BadRequestException("Admin cannot set status to CANCELLED")
        }

        val order = orderRepository.findById(orderId)
            .orElseThrow { NotFoundException("Order not found") }

        if (order.status == OrderStatus.CANCELLED) {
            throw BadRequestException("Cancelled order status cannot be changed")
        }

        order.status = newStatus
    }
}

@Service
class StatisticsService(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val mapper: Mapper
) {


    fun userOrders(userId: Long): List<OrderResponse> =
        orderRepository.findAllByUserId(userId).map { order ->
            val payment = paymentRepository.findByOrderId(order.id!!)
            mapper.toOrderResponse(order, payment)
        }


    fun userPayments(userId: Long): List<PaymentResponse> =
        paymentRepository.findAllByUserId(userId)
            .map { mapper.toPaymentResponse(it) }


    fun userMonthlyStats(userId: Long, year: Int, month: Int): UserMonthlyOrderStatsResponse {
        val ym = YearMonth.of(year, month)
        val from = ym.atDay(1).atStartOfDay()
        val to = ym.atEndOfMonth().atTime(23, 59, 59)

        val count = orderRepository.countByUserIdAndOrderDateBetween(userId, from, to)
        val total = orderRepository.sumTotalAmountByUserAndPeriod(userId, from, to)

        return UserMonthlyOrderStatsResponse(
            userId = userId,
            year = year,
            month = month,
            orderCount = count,
            totalAmount = total
        )
    }

    fun userProductStats(
        userId: Long,
        from: LocalDate,
        to: LocalDate
    ): UserProductOrderStatsResponse {
        val fromDt = from.atStartOfDay()
        val toDt = to.atTime(23, 59, 59)

        val items = orderItemRepository
            .findAllByOrderUserIdAndOrderOrderDateBetween(userId, fromDt, toDt)

        if (items.isEmpty()) {
            return UserProductOrderStatsResponse(
                userId = userId,
                from = fromDt,
                to = toDt,
                items = emptyList(),
                totalAmount = BigDecimal.ZERO
            )
        }

        val grouped = items.groupBy { it.product }

        val statsItems = grouped.map { (product, list) ->
            val totalQty = list.sumOf { it.quantity.toLong() }
            val orderCount = list.map { it.order.id }.toSet().size.toLong()
            val totalAmount = list.fold(BigDecimal.ZERO) { acc, item ->
                acc + item.totalPrice
            }
            UserProductOrderStatsItem(
                productId = product.id!!,
                productName = product.name,
                totalQuantity = totalQty,
                orderCount = orderCount,
                totalAmount = totalAmount
            )
        }

        val globalTotal = statsItems.fold(BigDecimal.ZERO) { acc, it ->
            acc + it.totalAmount
        }

        return UserProductOrderStatsResponse(
            userId = userId,
            from = fromDt,
            to = toDt,
            items = statsItems,
            totalAmount = globalTotal
        )
    }


    fun productUserCount(productId: Long): ProductUserCountResponse {
        val product = productRepository.findById(productId)
            .orElseThrow { NotFoundException("Product not found") }

        val count = orderItemRepository.countDistinctUsersByProductId(productId)

        return ProductUserCountResponse(
            productId = product.id!!,
            productName = product.name,
            userCount = count
        )
    }
}
