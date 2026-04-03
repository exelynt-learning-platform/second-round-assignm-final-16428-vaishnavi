package com.exelyent.task.controller;


import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.OrderRequest;
import com.exelyent.task.security.CustomUserDetails;
import com.exelyent.task.services.OrderService;

@RestController
@RequestMapping("/orders")

public class OrderController {

    private final OrderService orderService;
    

    public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
    public ResponseEntity<ApiResponse.OrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrderFromCart(userDetails.getId(), request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse.OrderResponse> getOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(userDetails.getId(), orderId));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse.PageResponse<ApiResponse.OrderResponse>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrders(userDetails.getId(), pageable));
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse.OrderResponse> cancelOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userDetails.getId(), orderId));
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse.PageResponse<ApiResponse.OrderResponse>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse.OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
}