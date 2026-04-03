package com.exelyent.task.services;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.OrderRequest;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    ApiResponse.OrderResponse createOrderFromCart(Long userId, OrderRequest request);

    ApiResponse.OrderResponse getOrderById(Long userId, Long orderId);

    ApiResponse.PageResponse<ApiResponse.OrderResponse> getUserOrders(Long userId, Pageable pageable);

    ApiResponse.OrderResponse cancelOrder(Long userId, Long orderId);

    ApiResponse.PageResponse<ApiResponse.OrderResponse> getAllOrders(Pageable pageable);

    ApiResponse.OrderResponse updateOrderStatus(Long orderId, String status);
}