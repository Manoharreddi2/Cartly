package com.cartly.order.controller;

import com.cartly.order.dto.*;
import com.cartly.order.service.OrderService;
import com.cartly.order.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody OrderRequest request) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        String userEmail = jwtUtils.getEmailFromHeader(authHeader);

        OrderResponse response = orderService.createOrder(userId, userEmail, request, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        boolean isAdmin = jwtUtils.isAdmin(authHeader);

        List<OrderResponse> orders = isAdmin ? orderService.getAllOrders() : orderService.getUserOrders(userId);
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.ok("Order retrieved", response));
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        OrderResponse response = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.ok("Order retrieved", response));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.ok("Order status updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long userId = headerUserId != null ? headerUserId : jwtUtils.getUserIdFromHeader(authHeader);
        OrderResponse response = orderService.cancelOrder(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled", response));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<OrderStatsResponse>> getStats() {
        OrderStatsResponse stats = orderService.getStats();
        return ResponseEntity.ok(ApiResponse.ok("Order statistics retrieved", stats));
    }
}
