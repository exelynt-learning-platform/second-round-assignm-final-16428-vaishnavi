package com.exelyent.task.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.CartRequest;
import com.exelyent.task.security.CustomUserDetails;
import com.exelyent.task.services.CartService;

@RestController
@RequestMapping("/cart")

public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
    public ResponseEntity<ApiResponse.CartResponse> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCartForUser(userDetails.getId()));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse.CartResponse> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CartRequest.AddItem request) {
        return ResponseEntity.ok(cartService.addItemToCart(userDetails.getId(), request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse.CartResponse> updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("cartItemId") Long cartItemId,
            @Valid @RequestBody CartRequest.UpdateItem request) {
        return ResponseEntity.ok(cartService.updateCartItem(userDetails.getId(), cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse.CartResponse> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("cartItemId") Long cartItemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userDetails.getId(), cartItemId));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse.CartResponse> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(cartService.clearCart(userDetails.getId()));
    }
}