package com.exelyent.task.services;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.CartRequest;

public interface CartService {

    ApiResponse.CartResponse getCartForUser(Long userId);

    ApiResponse.CartResponse addItemToCart(Long userId, CartRequest.AddItem request);

    ApiResponse.CartResponse updateCartItem(Long userId, Long cartItemId, CartRequest.UpdateItem request);

    ApiResponse.CartResponse removeItemFromCart(Long userId, Long cartItemId);

    ApiResponse.CartResponse clearCart(Long userId);
}