package com.exelyent.task.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exelyent.task.dto.ApiResponse;
import com.exelyent.task.dto.CartRequest;
import com.exelyent.task.entity.Cart;
import com.exelyent.task.entity.CartItem;
import com.exelyent.task.entity.Product;
import com.exelyent.task.exception.BusinessException;
import com.exelyent.task.exception.ResourceNotFoundException;
import com.exelyent.task.repository.CartItemRepo;
import com.exelyent.task.repository.CartRepo;
import com.exelyent.task.repository.ProductRepo;
import com.exelyent.task.repository.UserRepo;
import com.exelyent.task.services.CartService;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepo cartRepository;
    private final CartItemRepo cartItemRepository;
    private final ProductRepo productRepository;
    private final UserRepo userRepository;

    public CartServiceImpl(CartRepo cartRepository, CartItemRepo cartItemRepository,
                           ProductRepo productRepository, UserRepo userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse.CartResponse getCartForUser(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public ApiResponse.CartResponse addItemToCart(Long userId, CartRequest.AddItem request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findByIdAndActiveTrue(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!product.hasEnoughStock(request.getQuantity())) {
            throw new BusinessException(
                    String.format("Insufficient stock. Available: %d, Requested: %d",
                            product.getStockQuantity(), request.getQuantity()));
        }

        Optional<CartItem> existingItem =
                cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQty = item.getQuantity() + request.getQuantity();
            if (!product.hasEnoughStock(newQty)) {
                throw new BusinessException(
                        String.format("Cannot add %d more. Available stock: %d, already in cart: %d",
                                request.getQuantity(), product.getStockQuantity(), item.getQuantity()));
            }
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            newItem.setPriceAtAddTime(product.getPrice());
            cart.addItem(newItem);
        }

        cart = cartRepository.save(cart);
        log.info("Item added to cart: userId={}, productId={}", userId, product.getId());
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public ApiResponse.CartResponse updateCartItem(Long userId, Long cartItemId, CartRequest.UpdateItem request) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        Product product = item.getProduct();
        if (!product.hasEnoughStock(request.getQuantity())) {
            throw new BusinessException(
                    String.format("Insufficient stock. Available: %d", product.getStockQuantity()));
        }

        item.setQuantity(request.getQuantity());
        cart = cartRepository.save(cart);
        log.info("Cart item updated: userId={}, cartItemId={}", userId, cartItemId);
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public ApiResponse.CartResponse removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        cart.removeItem(item);
        cart = cartRepository.save(cart);
        log.info("Cart item removed: userId={}, cartItemId={}", userId, cartItemId);
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public ApiResponse.CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.clear();
        cart = cartRepository.save(cart);
        log.info("Cart cleared: userId={}", userId);
        return mapToResponse(cart);
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    private ApiResponse.CartResponse mapToResponse(Cart cart) {
        List<ApiResponse.CartItemResponse> items = cart.getCartItems().stream()
                .map(ci -> {
                    ApiResponse.CartItemResponse item = new ApiResponse.CartItemResponse();
                    item.setId(ci.getId());
                    item.setProductId(ci.getProduct().getId());
                    item.setProductName(ci.getProduct().getName());
                    item.setProductImageUrl(ci.getProduct().getImageUrl());
                    item.setUnitPrice(ci.getPriceAtAddTime());
                    item.setQuantity(ci.getQuantity());
                    item.setSubtotal(ci.getSubtotal());
                    item.setInStock(ci.getProduct().isInStock());
                    return item;
                })
                .toList();

        ApiResponse.CartResponse response = new ApiResponse.CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setItems(items);
        response.setTotalItems(cart.getTotalItems());
        response.setTotalPrice(cart.getTotalPrice());
        response.setUpdatedAt(cart.getUpdatedAt());
        return response;
    }
}