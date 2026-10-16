package com.cartly.order;

import com.cartly.order.dto.OrderItemDto;
import com.cartly.order.dto.OrderRequest;
import com.cartly.order.dto.OrderResponse;
import com.cartly.order.dto.OrderStatsResponse;
import com.cartly.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    @Test
    void testCreateOrderWithItems() {
        OrderRequest request = OrderRequest.builder()
                .recipientName("Alice Smith")
                .recipientPhone("+1 555-9876")
                .streetAddress("456 Elm St")
                .city("Portland")
                .state("OR")
                .zipCode("97201")
                .country("United States")
                .paymentMethod("CARD")
                .cardNumber("4111111111111111")
                .items(List.of(
                        OrderItemDto.builder()
                                .productId(1L)
                                .productName("Apple iPhone 17")
                                .price(new BigDecimal("1199.00"))
                                .discountedPrice(new BigDecimal("1079.10"))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderResponse order = orderService.createOrder(10L, "alice@example.com", request, null);
        assertNotNull(order);
        assertNotNull(order.getOrderNumber());
        assertEquals("CONFIRMED", order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals("Portland", order.getCity());

        // Update status
        OrderResponse updated = orderService.updateOrderStatus(order.getId(), "PROCESSING");
        assertEquals("PROCESSING", updated.getStatus());
    }

    @Test
    void testGetStats() {
        OrderStatsResponse stats = orderService.getStats();
        assertNotNull(stats);
        assertTrue(stats.getTotalOrders() >= 1);
    }
}
