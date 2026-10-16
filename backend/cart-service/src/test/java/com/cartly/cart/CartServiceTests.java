package com.cartly.cart;

import com.cartly.cart.dto.CartItemRequest;
import com.cartly.cart.dto.CartResponse;
import com.cartly.cart.dto.WishlistRequest;
import com.cartly.cart.dto.WishlistResponse;
import com.cartly.cart.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CartServiceTests {

    @Autowired
    private CartService cartService;

    @Test
    void testCartOperations() {
        Long userId = 99L;

        // Add item
        CartItemRequest item = CartItemRequest.builder()
                .productId(1L)
                .productName("Apple iPhone 17")
                .productImage("https://example.com/iphone.jpg")
                .price(new BigDecimal("1199.00"))
                .discountedPrice(new BigDecimal("1079.10"))
                .quantity(1)
                .build();

        CartResponse cart = cartService.addItem(userId, item);
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(1, cart.getTotalItems());

        // Update quantity
        cart = cartService.updateItemQuantity(userId, 1L, 2);
        assertEquals(2, cart.getTotalItems());

        // Remove item
        cart = cartService.removeItem(userId, 1L);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void testWishlistOperations() {
        Long userId = 99L;

        WishlistRequest req = WishlistRequest.builder()
                .productId(2L)
                .productName("Samsung Galaxy S26")
                .price(new BigDecimal("1099.00"))
                .discountedPrice(new BigDecimal("967.12"))
                .category("Mobiles")
                .build();

        WishlistResponse saved = cartService.addToWishlist(userId, req);
        assertNotNull(saved.getId());

        List<WishlistResponse> wishlist = cartService.getWishlist(userId);
        assertEquals(1, wishlist.size());

        // Move to cart
        CartResponse cart = cartService.moveToCart(userId, 2L);
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());

        wishlist = cartService.getWishlist(userId);
        assertEquals(0, wishlist.size());
    }
}
