package com.cartly.payment.controller;

import com.cartly.payment.dto.ApiResponse;
import com.cartly.payment.dto.PaymentRequest;
import com.cartly.payment.dto.PaymentResponse;
import com.cartly.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment processed successfully", response));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByOrderId(@PathVariable Long orderId) {
        PaymentResponse response = paymentService.getByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.ok("Payment retrieved", response));
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(@PathVariable Long orderId) {
        PaymentResponse response = paymentService.refundPayment(orderId);
        return ResponseEntity.ok(ApiResponse.ok("Payment refunded successfully", response));
    }
}
