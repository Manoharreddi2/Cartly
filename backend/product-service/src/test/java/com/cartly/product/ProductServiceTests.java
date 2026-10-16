package com.cartly.product;

import com.cartly.product.dto.ProductPageResponse;
import com.cartly.product.dto.ProductRequest;
import com.cartly.product.dto.ProductResponse;
import com.cartly.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductServiceTests {

    @Autowired
    private ProductService productService;

    @Test
    void testGetProductsPagination() {
        ProductPageResponse response = productService.getProducts(null, null, null, null, null, null, "newest", 0, 10);
        assertNotNull(response);
        assertTrue(response.getContent().size() > 0);
        assertTrue(response.getTotalElements() >= 16);
    }

    @Test
    void testSearchByKeyword() {
        ProductPageResponse response = productService.getProducts("iPhone", null, null, null, null, null, "newest", 0, 10);
        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertTrue(response.getContent().get(0).getName().contains("iPhone"));
    }

    @Test
    void testFilterByCategory() {
        ProductPageResponse response = productService.getProducts(null, "Shoes", null, null, null, null, "newest", 0, 10);
        assertNotNull(response);
        assertFalse(response.getContent().isEmpty());
        assertEquals("Shoes", response.getContent().get(0).getCategory());
    }

    @Test
    void testCreateAndGetProduct() {
        ProductRequest request = ProductRequest.builder()
                .name("Test Gaming Mouse")
                .description("High-precision optical gaming mouse with RGB")
                .price(new BigDecimal("49.99"))
                .discount(10)
                .category("Electronics")
                .brand("Logitech")
                .stock(100)
                .imageUrl("https://example.com/mouse.jpg")
                .rating(4.6)
                .isFeatured(false)
                .build();

        ProductResponse created = productService.createProduct(request);
        assertNotNull(created.getId());
        assertEquals("Test Gaming Mouse", created.getName());
        assertEquals(new BigDecimal("44.99"), created.getDiscountedPrice());

        ProductResponse fetched = productService.getProductById(created.getId());
        assertEquals(created.getId(), fetched.getId());
    }
}
