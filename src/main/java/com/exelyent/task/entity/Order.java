package com.exelyent.task.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String orderNumber = UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 12);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 12, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    // Shipping Address (embedded)
    @NotBlank
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    @NotBlank
    private String shippingCity;
    @NotBlank
    private String shippingState;
    @NotBlank
    private String shippingPostalCode;
    @NotBlank
    private String shippingCountry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Stripe Payment Intent ID
    @Column(unique = true)
    private String stripePaymentIntentId;

    // Stripe Charge ID (after successful payment)
    private String stripeChargeId;

    private String paymentFailureReason;

    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public BigDecimal getGrandTotal() {
        BigDecimal total = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        BigDecimal shipping = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        return total.add(shipping).add(tax);
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
    }

    public enum PaymentStatus {
        PENDING, PROCESSING, PAID, FAILED, REFUNDED, PARTIALLY_REFUNDED
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(BigDecimal shippingCost) {
		this.shippingCost = shippingCost;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getShippingAddressLine1() {
		return shippingAddressLine1;
	}

	public void setShippingAddressLine1(String shippingAddressLine1) {
		this.shippingAddressLine1 = shippingAddressLine1;
	}

	public String getShippingAddressLine2() {
		return shippingAddressLine2;
	}

	public void setShippingAddressLine2(String shippingAddressLine2) {
		this.shippingAddressLine2 = shippingAddressLine2;
	}

	public String getShippingCity() {
		return shippingCity;
	}

	public void setShippingCity(String shippingCity) {
		this.shippingCity = shippingCity;
	}

	public String getShippingState() {
		return shippingState;
	}

	public void setShippingState(String shippingState) {
		this.shippingState = shippingState;
	}

	public String getShippingPostalCode() {
		return shippingPostalCode;
	}

	public void setShippingPostalCode(String shippingPostalCode) {
		this.shippingPostalCode = shippingPostalCode;
	}

	public String getShippingCountry() {
		return shippingCountry;
	}

	public void setShippingCountry(String shippingCountry) {
		this.shippingCountry = shippingCountry;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getStripePaymentIntentId() {
		return stripePaymentIntentId;
	}

	public void setStripePaymentIntentId(String stripePaymentIntentId) {
		this.stripePaymentIntentId = stripePaymentIntentId;
	}

	public String getStripeChargeId() {
		return stripeChargeId;
	}

	public void setStripeChargeId(String stripeChargeId) {
		this.stripeChargeId = stripeChargeId;
	}

	public String getPaymentFailureReason() {
		return paymentFailureReason;
	}

	public void setPaymentFailureReason(String paymentFailureReason) {
		this.paymentFailureReason = paymentFailureReason;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Order(Long id, String orderNumber, User user, List<OrderItem> orderItems, BigDecimal totalAmount,
			BigDecimal shippingCost, BigDecimal taxAmount, @NotBlank String shippingAddressLine1,
			String shippingAddressLine2, @NotBlank String shippingCity, @NotBlank String shippingState,
			@NotBlank String shippingPostalCode, @NotBlank String shippingCountry, OrderStatus status,
			PaymentStatus paymentStatus, String stripePaymentIntentId, String stripeChargeId,
			String paymentFailureReason, LocalDateTime paidAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.user = user;
		this.orderItems = orderItems;
		this.totalAmount = totalAmount;
		this.shippingCost = shippingCost;
		this.taxAmount = taxAmount;
		this.shippingAddressLine1 = shippingAddressLine1;
		this.shippingAddressLine2 = shippingAddressLine2;
		this.shippingCity = shippingCity;
		this.shippingState = shippingState;
		this.shippingPostalCode = shippingPostalCode;
		this.shippingCountry = shippingCountry;
		this.status = status;
		this.paymentStatus = paymentStatus;
		this.stripePaymentIntentId = stripePaymentIntentId;
		this.stripeChargeId = stripeChargeId;
		this.paymentFailureReason = paymentFailureReason;
		this.paidAt = paidAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}