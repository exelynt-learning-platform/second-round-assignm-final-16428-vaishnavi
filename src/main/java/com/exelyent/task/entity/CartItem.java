package com.exelyent.task.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))

public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    // Snapshot of price at time of adding to cart
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAtAddTime;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public BigDecimal getSubtotal() {
        if (priceAtAddTime == null || quantity == null) return BigDecimal.ZERO;
        return priceAtAddTime.multiply(BigDecimal.valueOf(quantity));
    }
    
    

    public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}



	public Product getProduct() {
		return product;
	}



	public void setProduct(Product product) {
		this.product = product;
	}



	public BigDecimal getPriceAtAddTime() {
		return priceAtAddTime;
	}



	public void setPriceAtAddTime(BigDecimal priceAtAddTime) {
		this.priceAtAddTime = priceAtAddTime;
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



	public Cart getCart() {
		return cart;
	}



	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}



	public void setCart(Cart cart) {
        this.cart = cart;
    }



	public Integer getQuantity() {
		// TODO Auto-generated method stub
		return quantity;
	}



	public CartItem(Long id, Cart cart, @NotNull Product product, @NotNull @Min(1) Integer quantity,
			BigDecimal priceAtAddTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.cart = cart;
		this.product = product;
		this.quantity = quantity;
		this.priceAtAddTime = priceAtAddTime;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}



	public CartItem() {
		super();
	}
	
}