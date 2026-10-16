package com.cartly.order.service;

import com.cartly.order.dto.*;
import com.cartly.order.entity.Order;
import com.cartly.order.entity.OrderItem;
import com.cartly.order.exception.BadRequestException;
import com.cartly.order.exception.ResourceNotFoundException;
import com.cartly.order.repository.OrderItemRepository;
import com.cartly.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;

    @Value("${app.services.cart-url:http://localhost:8085}")
    private String cartServiceUrl;

    @Value("${app.services.inventory-url:http://localhost:8088}")
    private String inventoryServiceUrl;

    @Value("${app.services.payment-url:http://localhost:8087}")
    private String paymentServiceUrl;

    @Value("${app.services.notification-url:http://localhost:8089}")
    private String notificationServiceUrl;

    @Transactional
    public OrderResponse createOrder(Long userId, String userEmail, OrderRequest request, String authHeader) {
        List<OrderItemDto> itemsToOrder = request.getItems();

        // 1. If items not explicitly provided, fetch from cart-service
        if (itemsToOrder == null || itemsToOrder.isEmpty()) {
            itemsToOrder = fetchItemsFromCart(userId, authHeader);
        }

        if (itemsToOrder == null || itemsToOrder.isEmpty()) {
            throw new BadRequestException("Cannot create order: Shopping cart is empty.");
        }

        // 2. Check and reserve inventory
        Map<Long, Integer> itemQuantities = itemsToOrder.stream()
                .collect(Collectors.toMap(OrderItemDto::getProductId, OrderItemDto::getQuantity, Integer::sum));
        reserveInventory(itemQuantities);

        // 3. Calculate financial totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountedSubtotal = BigDecimal.ZERO;

        for (OrderItemDto item : itemsToOrder) {
            BigDecimal rawPrice = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            BigDecimal effectivePrice = item.getDiscountedPrice() != null ? item.getDiscountedPrice() : rawPrice;
            BigDecimal quantityBd = BigDecimal.valueOf(item.getQuantity());

            subtotal = subtotal.add(rawPrice.multiply(quantityBd));
            discountedSubtotal = discountedSubtotal.add(effectivePrice.multiply(quantityBd));
        }

        BigDecimal discount = subtotal.subtract(discountedSubtotal);
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            discount = BigDecimal.ZERO;
        }

        BigDecimal deliveryFee = discountedSubtotal.compareTo(new BigDecimal("100.00")) >= 0 ?
                BigDecimal.ZERO : new BigDecimal("10.00");

        BigDecimal tax = discountedSubtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = discountedSubtotal.add(deliveryFee).add(tax).setScale(2, RoundingMode.HALF_UP);

        // 4. Create and persist Order
        String orderNumber = "ORD-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .userEmail(userEmail)
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .discount(discount.setScale(2, RoundingMode.HALF_UP))
                .deliveryFee(deliveryFee.setScale(2, RoundingMode.HALF_UP))
                .tax(tax)
                .totalAmount(total)
                .status("CONFIRMED")
                .paymentMethod(request.getPaymentMethod().toUpperCase())
                .paymentStatus(request.getPaymentMethod().equalsIgnoreCase("CASH_ON_DELIVERY") ? "PENDING" : "PAID")
                .recipientName(request.getRecipientName())
                .recipientPhone(request.getRecipientPhone())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry() != null ? request.getCountry() : "United States")
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto itemDto : itemsToOrder) {
            BigDecimal effectivePrice = itemDto.getDiscountedPrice() != null ?
                    itemDto.getDiscountedPrice() : (itemDto.getPrice() != null ? itemDto.getPrice() : BigDecimal.ZERO);
            BigDecimal itemTotal = effectivePrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())).setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .order(savedOrder)
                    .productId(itemDto.getProductId())
                    .productName(itemDto.getProductName() != null ? itemDto.getProductName() : "Product #" + itemDto.getProductId())
                    .productImage(itemDto.getProductImage())
                    .price(itemDto.getPrice() != null ? itemDto.getPrice() : BigDecimal.ZERO)
                    .discountedPrice(itemDto.getDiscountedPrice())
                    .quantity(itemDto.getQuantity())
                    .itemTotal(itemTotal)
                    .build();
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        // 5. Initiate Payment
        String txnId = processPayment(savedOrder.getId(), userId, total, request.getPaymentMethod(), request);
        savedOrder.setTransactionId(txnId);
        orderRepository.save(savedOrder);

        // 6. Deduct inventory
        deductInventory(itemQuantities);

        // 7. Clear cart
        clearCart(userId, authHeader);

        // 8. Send Notification
        sendOrderNotification(userId, savedOrder.getOrderNumber(), total);

        log.info("Order successfully created: orderNumber={}, total={}", orderNumber, total);
        return toResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return toResponse(order);
    }

    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        String upperStatus = newStatus.toUpperCase();
        validateStatusTransition(order.getStatus(), upperStatus);
        order.setStatus(upperStatus);

        if ("DELIVERED".equals(upperStatus) && "PENDING".equals(order.getPaymentStatus())) {
            order.setPaymentStatus("PAID");
        } else if ("CANCELLED".equals(upperStatus)) {
            order.setPaymentStatus("REFUNDED");
            // Release inventory
            Map<Long, Integer> items = order.getItems().stream()
                    .collect(Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity, Integer::sum));
            releaseInventory(items);
            // Refund payment
            refundPayment(order.getId());
        }

        Order updated = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, upperStatus);
        return toResponse(updated);
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return;
        }

        Map<String, Set<String>> transitions = Map.of(
                "PLACED", Set.of("CONFIRMED", "CANCELLED"),
                "CONFIRMED", Set.of("PROCESSING", "CANCELLED"),
                "PROCESSING", Set.of("SHIPPED", "CANCELLED"),
                "SHIPPED", Set.of("OUT_FOR_DELIVERY"),
                "OUT_FOR_DELIVERY", Set.of("DELIVERED"),
                "DELIVERED", Set.of(),
                "CANCELLED", Set.of()
        );

        if (!transitions.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            throw new BadRequestException("Cannot change order status from " + currentStatus + " to " + newStatus);
        }
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("You are not authorized to cancel this order");
        }

        if ("SHIPPED".equals(order.getStatus()) || "OUT_FOR_DELIVERY".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus())) {
            throw new BadRequestException("Order cannot be cancelled in status: " + order.getStatus());
        }

        return updateOrderStatus(orderId, "CANCELLED");
    }

    public OrderStatsResponse getStats() {
        long total = orderRepository.count();
        long pending = orderRepository.countByStatus("CONFIRMED") + orderRepository.countByStatus("PROCESSING");
        long delivered = orderRepository.countByStatus("DELIVERED");
        BigDecimal revenue = orderRepository.calculateTotalRevenue();

        return OrderStatsResponse.builder()
                .totalOrders(total)
                .pendingOrders(pending)
                .deliveredOrders(delivered)
                .totalRevenue(revenue != null ? revenue.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .build();
    }

    // Helper microservice REST calls
    private List<OrderItemDto> fetchItemsFromCart(Long userId, String authHeader) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) headers.set("Authorization", authHeader);
            headers.set("X-User-Id", String.valueOf(userId));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    cartServiceUrl + "/api/cart",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map data = (Map) response.getBody().get("data");
                if (data != null && data.get("items") instanceof List<?> list) {
                    List<OrderItemDto> dtos = new ArrayList<>();
                    for (Object obj : list) {
                        if (obj instanceof Map itemMap) {
                            dtos.add(OrderItemDto.builder()
                                    .productId(Long.valueOf(itemMap.get("productId").toString()))
                                    .productName((String) itemMap.get("productName"))
                                    .productImage((String) itemMap.get("productImage"))
                                    .price(new BigDecimal(itemMap.get("price").toString()))
                                    .discountedPrice(itemMap.get("discountedPrice") != null ?
                                            new BigDecimal(itemMap.get("discountedPrice").toString()) : null)
                                    .quantity(Integer.valueOf(itemMap.get("quantity").toString()))
                                    .build());
                        }
                    }
                    return dtos;
                }
            }
        } catch (Exception e) {
            log.warn("Could not fetch cart from cart-service: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    private void reserveInventory(Map<Long, Integer> items) {
        try {
            Map<String, Object> req = Map.of("items", items);
            restTemplate.postForEntity(inventoryServiceUrl + "/api/inventory/reserve", req, Map.class);
        } catch (Exception e) {
            log.warn("Inventory reserve request failed: {}", e.getMessage());
        }
    }

    private void deductInventory(Map<Long, Integer> items) {
        try {
            Map<String, Object> req = Map.of("items", items);
            restTemplate.postForEntity(inventoryServiceUrl + "/api/inventory/deduct", req, Map.class);
        } catch (Exception e) {
            log.warn("Inventory deduct request failed: {}", e.getMessage());
        }
    }

    private void releaseInventory(Map<Long, Integer> items) {
        try {
            Map<String, Object> req = Map.of("items", items);
            restTemplate.postForEntity(inventoryServiceUrl + "/api/inventory/release", req, Map.class);
        } catch (Exception e) {
            log.warn("Inventory release request failed: {}", e.getMessage());
        }
    }

    private String processPayment(Long orderId, Long userId, BigDecimal amount, String method, OrderRequest request) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("orderId", orderId);
            req.put("userId", userId);
            req.put("amount", amount);
            req.put("paymentMethod", method);
            if (request.getCardNumber() != null) req.put("cardNumber", request.getCardNumber());
            if (request.getUpiId() != null) req.put("upiId", request.getUpiId());

            ResponseEntity<Map> response = restTemplate.postForEntity(paymentServiceUrl + "/api/payments/process", req, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map data = (Map) response.getBody().get("data");
                if (data != null && data.get("transactionId") != null) {
                    return data.get("transactionId").toString();
                }
            }
        } catch (Exception e) {
            log.warn("Payment service call failed: {}", e.getMessage());
        }
        return "TXN-" + System.currentTimeMillis();
    }

    private void refundPayment(Long orderId) {
        try {
            restTemplate.postForEntity(paymentServiceUrl + "/api/payments/refund/" + orderId, null, Map.class);
        } catch (Exception e) {
            log.warn("Refund payment call failed: {}", e.getMessage());
        }
    }

    private void clearCart(Long userId, String authHeader) {
        try {
            HttpHeaders headers = new HttpHeaders();
            if (authHeader != null) headers.set("Authorization", authHeader);
            headers.set("X-User-Id", String.valueOf(userId));
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.exchange(cartServiceUrl + "/api/cart", HttpMethod.DELETE, entity, Map.class);
        } catch (Exception e) {
            log.warn("Clear cart call failed: {}", e.getMessage());
        }
    }

    private void sendOrderNotification(Long userId, String orderNumber, BigDecimal amount) {
        try {
            Map<String, Object> req = Map.of(
                    "userId", userId,
                    "title", "Order " + orderNumber + " Placed!",
                    "message", "Your Cartly order of $" + amount + " has been placed successfully. Thank you for shopping with us!",
                    "type", "ORDER"
            );
            restTemplate.postForEntity(notificationServiceUrl + "/api/notifications/send", req, Map.class);
        } catch (Exception e) {
            log.warn("Notification send failed: {}", e.getMessage());
        }
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> itemResponses = o.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .price(item.getPrice())
                        .discountedPrice(item.getDiscountedPrice())
                        .quantity(item.getQuantity())
                        .itemTotal(item.getItemTotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .userId(o.getUserId())
                .userEmail(o.getUserEmail())
                .items(itemResponses)
                .subtotal(o.getSubtotal())
                .discount(o.getDiscount())
                .deliveryFee(o.getDeliveryFee())
                .tax(o.getTax())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .paymentMethod(o.getPaymentMethod())
                .paymentStatus(o.getPaymentStatus())
                .transactionId(o.getTransactionId())
                .recipientName(o.getRecipientName())
                .recipientPhone(o.getRecipientPhone())
                .streetAddress(o.getStreetAddress())
                .city(o.getCity())
                .state(o.getState())
                .zipCode(o.getZipCode())
                .country(o.getCountry())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
