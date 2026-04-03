package com.exelyent.task.dto;


import com.exelyent.task.entity.Order;
import com.exelyent.task.entity.Order.OrderStatus;
import com.exelyent.task.entity.Order.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

 
    public static class Success<T> {
        private boolean success = true;
        private String message;
        private T data;
        private LocalDateTime timestamp = LocalDateTime.now();
		public Success(boolean success, String message, T data, LocalDateTime timestamp) {
			super();
			this.success = success;
			this.message = message;
			this.data = data;
			this.timestamp = timestamp;
		}
		public Success() {
			super();
			// TODO Auto-generated constructor stub
		}
    }

  
    public static class Error {
        private boolean success = false;
        private int status;
        private String message;
        private Object errors;
        private LocalDateTime timestamp = LocalDateTime.now();
		public Error(boolean success, int status, String message, Object errors, LocalDateTime timestamp) {
			super();
			this.success = success;
			this.status = status;
			this.message = message;
			this.errors = errors;
			this.timestamp = timestamp;
		}
		public Error() {
			super();
			// TODO Auto-generated constructor stub
		}
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public Object getErrors() {
			return errors;
		}
		public void setErrors(Object errors) {
			this.errors = errors;
		}
		public LocalDateTime getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
		}
		
    }

    
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType = "Bearer";
        private Long expiresIn;
        private UserResponse user;
		public AuthResponse(String accessToken, String refreshToken, String tokenType, Long expiresIn,
				UserResponse user) {
			super();
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.tokenType = tokenType;
			this.expiresIn = expiresIn;
			this.user = user;
		}
		public AuthResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public String getAccessToken() {
			return accessToken;
		}
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		public String getRefreshToken() {
			return refreshToken;
		}
		public void setRefreshToken(String refreshToken) {
			this.refreshToken = refreshToken;
		}
		public String getTokenType() {
			return tokenType;
		}
		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}
		public Long getExpiresIn() {
			return expiresIn;
		}
		public void setExpiresIn(Long expiresIn) {
			this.expiresIn = expiresIn;
		}
		public UserResponse getUser() {
			return user;
		}
		public void setUser(UserResponse user) {
			this.user = user;
		}
		
    }

    
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String role;
        private boolean enabled;
        private LocalDateTime createdAt;
		public UserResponse(Long id, String username, String email, String firstName, String lastName, String phone,
				String role, boolean enabled, LocalDateTime createdAt) {
			super();
			this.id = id;
			this.username = username;
			this.email = email;
			this.firstName = firstName;
			this.lastName = lastName;
			this.phone = phone;
			this.role = role;
			this.enabled = enabled;
			this.createdAt = createdAt;
		}
		public UserResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getRole() {
			return role;
		}
		public void setRole(String role) {
			this.role = role;
		}
		public boolean isEnabled() {
			return enabled;
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		
    }

   
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private String imageUrl;
        private String category;
        private String brand;
        private boolean active;
        private boolean inStock;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
		public ProductResponse(Long id, String name, String description, BigDecimal price, Integer stockQuantity,
				String imageUrl, String category, String brand, boolean active, boolean inStock,
				LocalDateTime createdAt, LocalDateTime updatedAt) {
			super();
			this.id = id;
			this.name = name;
			this.description = description;
			this.price = price;
			this.stockQuantity = stockQuantity;
			this.imageUrl = imageUrl;
			this.category = category;
			this.brand = brand;
			this.active = active;
			this.inStock = inStock;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
		}
		public ProductResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public Integer getStockQuantity() {
			return stockQuantity;
		}
		public void setStockQuantity(Integer stockQuantity) {
			this.stockQuantity = stockQuantity;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getBrand() {
			return brand;
		}
		public void setBrand(String brand) {
			this.brand = brand;
		}
		public boolean isActive() {
			return active;
		}
		public void setActive(boolean active) {
			this.active = active;
		}
		public boolean isInStock() {
			return inStock;
		}
		public void setInStock(boolean inStock) {
			this.inStock = inStock;
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
		
    }

   
    public static class CartResponse {
        private Long id;
        private Long userId;
        private List<CartItemResponse> items;
        private int totalItems;
        private BigDecimal totalPrice;
        private LocalDateTime updatedAt;
		public CartResponse(Long id, Long userId, List<CartItemResponse> items, int totalItems, BigDecimal totalPrice,
				LocalDateTime updatedAt) {
			super();
			this.id = id;
			this.userId = userId;
			this.items = items;
			this.totalItems = totalItems;
			this.totalPrice = totalPrice;
			this.updatedAt = updatedAt;
		}
		public CartResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public List<CartItemResponse> getItems() {
			return items;
		}
		public void setItems(List<CartItemResponse> items) {
			this.items = items;
		}
		public int getTotalItems() {
			return totalItems;
		}
		public void setTotalItems(int totalItems) {
			this.totalItems = totalItems;
		}
		public BigDecimal getTotalPrice() {
			return totalPrice;
		}
		public void setTotalPrice(BigDecimal totalPrice) {
			this.totalPrice = totalPrice;
		}
		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}
		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}
		
    }
    
    public static class CartItemResponse {
        public CartItemResponse() {
			super();
		}
		public CartItemResponse(Long id, Long productId, String productName, String productImageUrl,
				BigDecimal unitPrice, Integer quantity, BigDecimal subtotal, boolean inStock) {
			super();
			this.id = id;
			this.productId = productId;
			this.productName = productName;
			this.productImageUrl = productImageUrl;
			this.unitPrice = unitPrice;
			this.quantity = quantity;
			this.subtotal = subtotal;
			this.inStock = inStock;
		}
		private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
        private boolean inStock;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getProductId() {
			return productId;
		}
		public void setProductId(Long productId) {
			this.productId = productId;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getProductImageUrl() {
			return productImageUrl;
		}
		public void setProductImageUrl(String productImageUrl) {
			this.productImageUrl = productImageUrl;
		}
		public BigDecimal getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public BigDecimal getSubtotal() {
			return subtotal;
		}
		public void setSubtotal(BigDecimal subtotal) {
			this.subtotal = subtotal;
		}
		public boolean isInStock() {
			return inStock;
		}
		public void setInStock(boolean inStock) {
			this.inStock = inStock;
		}
        
    }

    public static class OrderResponse {
        private Long id;
        private String orderNumber;
        private Long userId;
        private String username;
        private List<OrderItemResponse> orderItems;
        private BigDecimal totalAmount;
        private BigDecimal shippingCost;
        private BigDecimal taxAmount;
        private BigDecimal grandTotal;
        private String shippingAddressLine1;
        private String shippingAddressLine2;
        private String shippingCity;
        private String shippingState;
        private String shippingPostalCode;
        private String shippingCountry;
        private Order.OrderStatus status;
        private Order.PaymentStatus paymentStatus;
        private String stripePaymentIntentId;
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
		public OrderResponse(Long id, String orderNumber, Long userId, String username,
				List<OrderItemResponse> orderItems, BigDecimal totalAmount, BigDecimal shippingCost,
				BigDecimal taxAmount, BigDecimal grandTotal, String shippingAddressLine1, String shippingAddressLine2,
				String shippingCity, String shippingState, String shippingPostalCode, String shippingCountry,
				OrderStatus status, PaymentStatus paymentStatus, String stripePaymentIntentId, LocalDateTime paidAt,
				LocalDateTime createdAt, LocalDateTime updatedAt) {
			super();
			this.id = id;
			this.orderNumber = orderNumber;
			this.userId = userId;
			this.username = username;
			this.orderItems = orderItems;
			this.totalAmount = totalAmount;
			this.shippingCost = shippingCost;
			this.taxAmount = taxAmount;
			this.grandTotal = grandTotal;
			this.shippingAddressLine1 = shippingAddressLine1;
			this.shippingAddressLine2 = shippingAddressLine2;
			this.shippingCity = shippingCity;
			this.shippingState = shippingState;
			this.shippingPostalCode = shippingPostalCode;
			this.shippingCountry = shippingCountry;
			this.status = status;
			this.paymentStatus = paymentStatus;
			this.stripePaymentIntentId = stripePaymentIntentId;
			this.paidAt = paidAt;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
		}
		public OrderResponse() {
			super();
			// TODO Auto-generated constructor stub
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
		public Long getUserId() {
			return userId;
		}
		public void setUserId(Long userId) {
			this.userId = userId;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public List<OrderItemResponse> getOrderItems() {
			return orderItems;
		}
		public void setOrderItems(List<OrderItemResponse> orderItems) {
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
		public BigDecimal getGrandTotal() {
			return grandTotal;
		}
		public void setGrandTotal(BigDecimal grandTotal) {
			this.grandTotal = grandTotal;
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
		public Order.OrderStatus getStatus() {
			return status;
		}
		public void setStatus(Order.OrderStatus status) {
			this.status = status;
		}
		public Order.PaymentStatus getPaymentStatus() {
			return paymentStatus;
		}
		public void setPaymentStatus(Order.PaymentStatus paymentStatus) {
			this.paymentStatus = paymentStatus;
		}
		public String getStripePaymentIntentId() {
			return stripePaymentIntentId;
		}
		public void setStripePaymentIntentId(String stripePaymentIntentId) {
			this.stripePaymentIntentId = stripePaymentIntentId;
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
		
    }

    
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
		public OrderItemResponse(Long id, Long productId, String productName, String productImageUrl, Integer quantity,
				BigDecimal unitPrice, BigDecimal subtotal) {
			super();
			this.id = id;
			this.productId = productId;
			this.productName = productName;
			this.productImageUrl = productImageUrl;
			this.quantity = quantity;
			this.unitPrice = unitPrice;
			this.subtotal = subtotal;
		}
		public OrderItemResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getProductId() {
			return productId;
		}
		public void setProductId(Long productId) {
			this.productId = productId;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getProductImageUrl() {
			return productImageUrl;
		}
		public void setProductImageUrl(String productImageUrl) {
			this.productImageUrl = productImageUrl;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public BigDecimal getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}
		public BigDecimal getSubtotal() {
			return subtotal;
		}
		public void setSubtotal(BigDecimal subtotal) {
			this.subtotal = subtotal;
		}
		
    }

    
    public static class PaymentIntentResponse {
        private String clientSecret;
        private String paymentIntentId;
        private BigDecimal amount;
        private String currency;
        private String status;
        private Long orderId;
        private String orderNumber;
		public PaymentIntentResponse(String clientSecret, String paymentIntentId, BigDecimal amount, String currency,
				String status, Long orderId, String orderNumber) {
			super();
			this.clientSecret = clientSecret;
			this.paymentIntentId = paymentIntentId;
			this.amount = amount;
			this.currency = currency;
			this.status = status;
			this.orderId = orderId;
			this.orderNumber = orderNumber;
		}
		public PaymentIntentResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public String getClientSecret() {
			return clientSecret;
		}
		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}
		public String getPaymentIntentId() {
			return paymentIntentId;
		}
		public void setPaymentIntentId(String paymentIntentId) {
			this.paymentIntentId = paymentIntentId;
		}
		public BigDecimal getAmount() {
			return amount;
		}
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}
		public String getCurrency() {
			return currency;
		}
		public void setCurrency(String currency) {
			this.currency = currency;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Long getOrderId() {
			return orderId;
		}
		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}
		public String getOrderNumber() {
			return orderNumber;
		}
		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}
		
    }

 
    public static class PageResponse<T> {
        private List<T> content;
        private int pageNumber;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean last;
        private boolean first;
		public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages,
				boolean last, boolean first) {
			super();
			this.content = content;
			this.pageNumber = pageNumber;
			this.pageSize = pageSize;
			this.totalElements = totalElements;
			this.totalPages = totalPages;
			this.last = last;
			this.first = first;
		}
		public PageResponse() {
			super();
			// TODO Auto-generated constructor stub
		}
		public List<T> getContent() {
			return content;
		}
		public void setContent(List<T> content) {
			this.content = content;
		}
		public int getPageNumber() {
			return pageNumber;
		}
		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}
		public int getPageSize() {
			return pageSize;
		}
		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}
		public long getTotalElements() {
			return totalElements;
		}
		public void setTotalElements(long totalElements) {
			this.totalElements = totalElements;
		}
		public int getTotalPages() {
			return totalPages;
		}
		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}
		public boolean isLast() {
			return last;
		}
		public void setLast(boolean last) {
			this.last = last;
		}
		public boolean isFirst() {
			return first;
		}
		public void setFirst(boolean first) {
			this.first = first;
		}
		
		
    }
}