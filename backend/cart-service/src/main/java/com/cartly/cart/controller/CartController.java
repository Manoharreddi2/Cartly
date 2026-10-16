package com.cartly.cart.controller;

import com.cartly.cart.dto.*;
import com.cartly.cart.service.CartService;
import com.cartly.cart.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        CartResponse cart = cartService.getCartResponse(userId);
        return ResponseEntity.ok(ApiResponse.ok("Cart retrieved", cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody CartItemRequest request) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        CartResponse cart = cartService.addItem(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> body) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        int quantity = body.getOrDefault("quantity", 1);
        CartResponse cart = cartService.updateItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(ApiResponse.ok("Cart updated", cart));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long productId) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        CartResponse cart = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        CartResponse cart = cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", cart));
    }

    // Wishlist endpoints
    @GetMapping("/wishlist")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getWishlist(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        List<WishlistResponse> wishlist = cartService.getWishlist(userId);
        return ResponseEntity.ok(ApiResponse.ok("Wishlist retrieved", wishlist));
    }

    @PostMapping("/wishlist")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody WishlistRequest request) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        WishlistResponse item = cartService.addToWishlist(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to wishlist", item));
    }

    @DeleteMapping("/wishlist/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long productId) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        cartService.removeFromWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from wishlist", null));
    }

    @PostMapping("/wishlist/{productId}/move-to-cart")
    public ResponseEntity<ApiResponse<CartResponse>> moveToCart(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long productId) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        CartResponse cart = cartService.moveToCart(userId, productId);
        return ResponseEntity.ok(ApiResponse.ok("Item moved to cart", cart));
    }
}
