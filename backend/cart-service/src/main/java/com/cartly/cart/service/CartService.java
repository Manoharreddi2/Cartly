package com.cartly.cart.service;

import com.cartly.cart.dto.*;
import com.cartly.cart.entity.Cart;
import com.cartly.cart.entity.CartItem;
import com.cartly.cart.entity.WishlistItem;
import com.cartly.cart.exception.ResourceNotFoundException;
import com.cartly.cart.repository.CartItemRepository;
import com.cartly.cart.repository.CartRepository;
import com.cartly.cart.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final WishlistItemRepository wishlistItemRepository;

    @Transactional
    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
    }

    @Transactional(readOnly = true)
    public CartResponse getCartResponse(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(Long userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(request.getProductName() != null ? request.getProductName() : "Product #" + request.getProductId())
                    .productImage(request.getProductImage())
                    .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                    .discountedPrice(request.getDiscountedPrice() != null ? request.getDiscountedPrice() : request.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        return getCartResponse(userId);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart for productId: " + productId));

        if (quantity <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return getCartResponse(userId);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart for productId: " + productId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return getCartResponse(userId);
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
        return getCartResponse(userId);
    }

    // Wishlist operations
    public List<WishlistResponse> getWishlist(Long userId) {
        return wishlistItemRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toWishlistResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistResponse addToWishlist(Long userId, WishlistRequest request) {
        return wishlistItemRepository.findByUserIdAndProductId(userId, request.getProductId())
                .map(this::toWishlistResponse)
                .orElseGet(() -> {
                    WishlistItem item = WishlistItem.builder()
                            .userId(userId)
                            .productId(request.getProductId())
                            .productName(request.getProductName())
                            .productImage(request.getProductImage())
                            .price(request.getPrice())
                            .discountedPrice(request.getDiscountedPrice())
                            .category(request.getCategory())
                            .build();
                    return toWishlistResponse(wishlistItemRepository.save(item));
                });
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public CartResponse moveToCart(Long userId, Long productId) {
        WishlistItem wishlistItem = wishlistItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not in wishlist"));

        CartItemRequest cartRequest = CartItemRequest.builder()
                .productId(wishlistItem.getProductId())
                .productName(wishlistItem.getProductName())
                .productImage(wishlistItem.getProductImage())
                .price(wishlistItem.getPrice())
                .discountedPrice(wishlistItem.getDiscountedPrice())
                .quantity(1)
                .build();

        CartResponse response = addItem(userId, cartRequest);
        wishlistItemRepository.delete(wishlistItem);
        return response;
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountedSubtotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getItems()) {
            BigDecimal effectivePrice = item.getDiscountedPrice() != null ? item.getDiscountedPrice() : item.getPrice();
            BigDecimal rawItemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal effectiveItemTotal = effectivePrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            subtotal = subtotal.add(rawItemTotal);
            discountedSubtotal = discountedSubtotal.add(effectiveItemTotal);
            totalItems += item.getQuantity();

            itemResponses.add(CartItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productImage(item.getProductImage())
                    .price(item.getPrice())
                    .discountedPrice(item.getDiscountedPrice())
                    .quantity(item.getQuantity())
                    .itemTotal(effectiveItemTotal)
                    .build());
        }

        BigDecimal discount = subtotal.subtract(discountedSubtotal);
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        // Delivery fee: Free for orders over $100, else $10 (if cart has items)
        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (totalItems > 0) {
            deliveryFee = discountedSubtotal.compareTo(new BigDecimal("100.00")) >= 0 ?
                    BigDecimal.ZERO : new BigDecimal("10.00");
        }

        // 5% estimated tax
        BigDecimal tax = discountedSubtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = discountedSubtotal.add(deliveryFee).add(tax).setScale(2, RoundingMode.HALF_UP);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemResponses)
                .totalItems(totalItems)
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .discount(discount.setScale(2, RoundingMode.HALF_UP))
                .deliveryFee(deliveryFee.setScale(2, RoundingMode.HALF_UP))
                .tax(tax)
                .total(total)
                .build();
    }

    private WishlistResponse toWishlistResponse(WishlistItem item) {
        return WishlistResponse.builder()
                .id(item.getId())
                .userId(item.getUserId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .price(item.getPrice())
                .discountedPrice(item.getDiscountedPrice())
                .category(item.getCategory())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
