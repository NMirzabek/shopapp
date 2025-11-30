package com.example.shopapp

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun create(@RequestBody req: UserCreateRequest) = userService.create(req)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = userService.get(id)

    @GetMapping
    fun getAll() = userService.getAll()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: UserUpdateRequest) =
        userService.update(id, req)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = userService.delete(id)
}

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    fun create(@RequestBody req: CategoryCreateRequest) = categoryService.create(req)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = categoryService.get(id)

    @GetMapping
    fun getAll() = categoryService.getAll()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: CategoryUpdateRequest) =
        categoryService.update(id, req)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = categoryService.delete(id)
}

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    @PostMapping
    fun create(@RequestBody req: ProductCreateRequest) = productService.create(req)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = productService.get(id)

    @GetMapping
    fun getAll() = productService.getAll()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: ProductUpdateRequest) =
        productService.update(id, req)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = productService.delete(id)
}

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
    private val statisticsService: StatisticsService
) {

    @PostMapping
    fun createOrder(@RequestBody req: OrderCreateRequest) =
        orderService.createOrder(req)

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long) =
        orderService.getOrder(id)

    @GetMapping("/user/{userId}")
    fun getUserOrders(@PathVariable userId: Long) =
        statisticsService.userOrders(userId)

    @PostMapping("/{orderId}/cancel")
    fun cancelByUser(
        @PathVariable orderId: Long,
        @RequestParam userId: Long
    ) = orderService.cancelOrderByUser(orderId, userId)

    @PostMapping("/{orderId}/status")
    fun changeStatus(
        @PathVariable orderId: Long,
        @RequestParam status: OrderStatus
    ) = orderService.changeStatusByAdmin(orderId, status)
}

@RestController
@RequestMapping("/api/statistics")
class StatisticsController(
    private val statisticsService: StatisticsService
) {

    @GetMapping("/users/{userId}/orders")
    fun userOrders(@PathVariable userId: Long) =
        statisticsService.userOrders(userId)

    @GetMapping("/users/{userId}/payments")
    fun userPayments(@PathVariable userId: Long) =
        statisticsService.userPayments(userId)

    @GetMapping("/users/{userId}/monthly")
    fun monthlyStats(
        @PathVariable userId: Long,
        @RequestParam year: Int,
        @RequestParam month: Int
    ) = statisticsService.userMonthlyStats(userId, year, month)

    @GetMapping("/users/{userId}/products")
    fun userProductStats(
        @PathVariable userId: Long,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        from: LocalDate,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        to: LocalDate
    ) = statisticsService.userProductStats(userId, from, to)

    @GetMapping("/products/{productId}/users-count")
    fun productUserCount(@PathVariable productId: Long) =
        statisticsService.productUserCount(productId)
}
