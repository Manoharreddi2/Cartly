package com.cartly.payment.service;

import com.cartly.payment.dto.PaymentRequest;
import com.cartly.payment.dto.PaymentResponse;
import com.cartly.payment.entity.PaymentTransaction;
import com.cartly.payment.exception.ResourceNotFoundException;
import com.cartly.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentTransactionRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        String method = request.getPaymentMethod().toUpperCase();
        String status = method.equals("CASH_ON_DELIVERY") ? "PENDING" : "SUCCESS";

        String transactionId = "TXN-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PaymentTransaction transaction = PaymentTransaction.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(method)
                .transactionId(transactionId)
                .status(status)
                .build();

        PaymentTransaction saved = paymentRepository.save(transaction);
        log.info("Processed payment for order {}: txnId={}, method={}, status={}",
                request.getOrderId(), transactionId, method, status);

        return toResponse(saved);
    }

    public PaymentResponse getByOrderId(Long orderId) {
        PaymentTransaction transaction = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found for order: " + orderId));
        return toResponse(transaction);
    }

    @Transactional
    public PaymentResponse refundPayment(Long orderId) {
        PaymentTransaction transaction = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment transaction not found for order: " + orderId));

        transaction.setStatus("REFUNDED");
        PaymentTransaction updated = paymentRepository.save(transaction);
        log.info("Refunded payment for order {}: txnId={}", orderId, transaction.getTransactionId());

        return toResponse(updated);
    }

    private PaymentResponse toResponse(PaymentTransaction t) {
        return PaymentResponse.builder()
                .id(t.getId())
                .orderId(t.getOrderId())
                .userId(t.getUserId())
                .amount(t.getAmount())
                .paymentMethod(t.getPaymentMethod())
                .transactionId(t.getTransactionId())
                .status(t.getStatus())
                .failureReason(t.getFailureReason())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
