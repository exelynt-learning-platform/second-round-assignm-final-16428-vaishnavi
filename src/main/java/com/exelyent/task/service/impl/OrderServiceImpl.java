package com.exelyent.task.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.OrderRequest;
import com.exelyent.task.entity.Cart;
import com.exelyent.task.entity.CartItem;
import com.exelyent.task.entity.Order;
import com.exelyent.task.entity.OrderItem;
import com.exelyent.task.entity.Product;
import com.exelyent.task.entity.User;
import com.exelyent.task.exception.BusinessException;
import com.exelyent.task.exception.ResourceNotFoundException;
import com.exelyent.task.repository.CartRepo;
import com.exelyent.task.repository.OrderRepo;
import com.exelyent.task.repository.ProductRepo;
import com.exelyent.task.repository.UserRepo;
import com.exelyent.task.services.OrderService;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepo orderRepository;
    private final CartRepo cartRepository;
    private final ProductRepo productRepository;
    private final UserRepo userRepository;

    public OrderServiceImpl(OrderRepo orderRepository, CartRepo cartRepository,
                            ProductRepo productRepository, UserRepo userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ApiResponse.OrderResponse createOrderFromCart(Long userId, OrderRequest request) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new BusinessException("Cart not found for user"));

        if (cart.getCartItems().isEmpty()) {
            throw new BusinessException("Cannot create order from an empty cart");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Order order = buildOrder(user, cart, request);

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItem.getProduct().getId()));
            product.reduceStock(cartItem.getQuantity());
            productRepository.save(product);
        }

        order = orderRepository.save(order);

        cart.clear();
        cartRepository.save(cart);

        log.info("Order created: orderId={}, userId={}, total={}", order.getId(), userId, order.getGrandTotal());
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse.OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse.PageResponse<ApiResponse.OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        Page<Order> page = orderRepository.findByUserId(userId, pageable);
        return buildPage(page);
    }

    @Override
    @Transactional
    public ApiResponse.OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getStatus() == Order.OrderStatus.SHIPPED ||
                order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new BusinessException("Cannot cancel an order that has been shipped or delivered");
        }
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BusinessException("Order is already cancelled");
        }

        order.getOrderItems().forEach(item -> {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        });

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setPaymentStatus(Order.PaymentStatus.REFUNDED);
        order = orderRepository.save(order);

        log.info("Order cancelled: orderId={}, userId={}", orderId, userId);
        return mapToResponse(order);
    }

    // ─── Admin ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ApiResponse.PageResponse<ApiResponse.OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return buildPage(page);
    }

    @Override
    @Transactional
    public ApiResponse.OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid order status: " + status);
        }
        order = orderRepository.save(order);
        log.info("Order status updated: orderId={}, newStatus={}", orderId, status);
        return mapToResponse(order);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Order buildOrder(User user, Cart cart, OrderRequest request) {
        BigDecimal totalAmount = cart.getTotalPrice();
        BigDecimal shippingCost = calculateShipping(totalAmount);
        BigDecimal taxAmount = totalAmount.multiply(new BigDecimal("0.08"));

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setShippingCost(shippingCost);
        order.setTaxAmount(taxAmount);
        order.setShippingAddressLine1(request.getShippingAddressLine1());
        order.setShippingAddressLine2(request.getShippingAddressLine2());
        order.setShippingCity(request.getShippingCity());
        order.setShippingState(request.getShippingState());
        order.setShippingPostalCode(request.getShippingPostalCode());
        order.setShippingCountry(request.getShippingCountry());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProduct(cartItem.getProduct());
                    item.setProductName(cartItem.getProduct().getName());
                    item.setProductImageUrl(cartItem.getProduct().getImageUrl());
                    item.setQuantity(cartItem.getQuantity());
                    item.setUnitPrice(cartItem.getPriceAtAddTime());
                    return item;
                })
                .toList();

        order.getOrderItems().addAll(orderItems);
        return order;
    }

    private BigDecimal calculateShipping(BigDecimal orderTotal) {
        return orderTotal.compareTo(new BigDecimal("50.00")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("5.99");
    }

    public ApiResponse.OrderResponse mapToResponse(Order order) {
        List<ApiResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(oi -> {
                    ApiResponse.OrderItemResponse item = new ApiResponse.OrderItemResponse();
                    item.setId(oi.getId());
                    item.setProductId(oi.getProduct().getId());
                    item.setProductName(oi.getProductName());
                    item.setProductImageUrl(oi.getProductImageUrl());
                    item.setQuantity(oi.getQuantity());
                    item.setUnitPrice(oi.getUnitPrice());
                    item.setSubtotal(oi.getSubtotal());
                    return item;
                })
                .toList();

        ApiResponse.OrderResponse response = new ApiResponse.OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUser().getId());
        response.setUsername(order.getUser().getUsername());
        response.setOrderItems(itemResponses);
        response.setTotalAmount(order.getTotalAmount());
        response.setShippingCost(order.getShippingCost());
        response.setTaxAmount(order.getTaxAmount());
        response.setGrandTotal(order.getGrandTotal());
        response.setShippingAddressLine1(order.getShippingAddressLine1());
        response.setShippingAddressLine2(order.getShippingAddressLine2());
        response.setShippingCity(order.getShippingCity());
        response.setShippingState(order.getShippingState());
        response.setShippingPostalCode(order.getShippingPostalCode());
        response.setShippingCountry(order.getShippingCountry());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setStripePaymentIntentId(order.getStripePaymentIntentId());
        response.setPaidAt(order.getPaidAt());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

    private ApiResponse.PageResponse<ApiResponse.OrderResponse> buildPage(Page<Order> page) {
        ApiResponse.PageResponse<ApiResponse.OrderResponse> response = new ApiResponse.PageResponse<>();
        response.setContent(page.getContent().stream().map(this::mapToResponse).toList());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }
}