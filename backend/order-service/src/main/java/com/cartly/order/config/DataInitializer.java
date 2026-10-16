package com.cartly.order.config;

import com.cartly.order.entity.Order;
import com.cartly.order.entity.OrderItem;
import com.cartly.order.repository.OrderItemRepository;
import com.cartly.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public void run(String... args) {
        if (orderRepository.count() == 0) {
            Order sampleOrder = Order.builder()
                    .orderNumber("ORD-17270001-A1B2")
                    .userId(2L)
                    .userEmail("customer@cartly.com")
                    .subtotal(new BigDecimal("1199.00"))
                    .discount(new BigDecimal("119.90"))
                    .deliveryFee(BigDecimal.ZERO)
                    .tax(new BigDecimal("53.96"))
                    .totalAmount(new BigDecimal("1133.06"))
                    .status("SHIPPED")
                    .paymentMethod("CARD")
                    .paymentStatus("PAID")
                    .transactionId("TXN-17270001-SUCCESS")
                    .recipientName("John Doe")
                    .recipientPhone("+1 555-0199")
                    .streetAddress("742 Evergreen Terrace")
                    .city("Springfield")
                    .state("OR")
                    .zipCode("97477")
                    .country("United States")
                    .build();

            Order savedOrder = orderRepository.save(sampleOrder);

            OrderItem item = OrderItem.builder()
                    .order(savedOrder)
                    .productId(1L)
                    .productName("Apple iPhone 17")
                    .productImage("https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600")
                    .price(new BigDecimal("1199.00"))
                    .discountedPrice(new BigDecimal("1079.10"))
                    .quantity(1)
                    .itemTotal(new BigDecimal("1079.10"))
                    .build();

            orderItemRepository.save(item);
            log.info("Initialized sample order: {}", savedOrder.getOrderNumber());
        }
    }
}
