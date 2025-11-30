package com.example.shopapp

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(name = "created_at", nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "users")
class User(

    @Column(nullable = false, length = 50, unique = true)
    var username: String,

    @Column(name = "full_name", nullable = false, length = 100)
    var fullName: String,

    @Column(nullable = false, length = 100, unique = true)
    var email: String,

    @Column(columnDefinition = "text")
    var address: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var role: UserRole = UserRole.USER

) : BaseEntity()

@Entity
@Table(name = "category")
class Category(

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(columnDefinition = "text")
    var description: String? = null

) : BaseEntity()

@Entity
@Table(name = "product")
class Product(

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(name = "stock_count", nullable = false)
    var stockCount: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category

) : BaseEntity()

@Entity
@Table(name = "orders")   // DBda "Order" bo‘lsa ham xavfsizroq nom
class Order(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "order_date", nullable = false)
    var orderDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: OrderStatus = OrderStatus.PENDING,

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<OrderItem> = mutableListOf()

) : BaseEntity()

@Entity
@Table(name = "orderitem")
class OrderItem(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product,

    @Column(nullable = false)
    var quantity: Int,

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    var unitPrice: BigDecimal,

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    var totalPrice: BigDecimal

) : BaseEntity()

@Entity
@Table(name = "payment")
class Payment(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    var paymentMethod: PaymentMethod,

    @Column(nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal,

    @Column(name = "payment_date", nullable = false)
    var paymentDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: PaymentStatus = PaymentStatus.PAID

) : BaseEntity()
