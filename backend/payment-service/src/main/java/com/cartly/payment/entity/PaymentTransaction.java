package com.cartly.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_payment_orderId", columnList = "orderId"),
    @Index(name = "idx_payment_userId", columnList = "userId"),
    @Index(name = "idx_payment_transactionId", columnList = "transactionId", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 30)
    private String paymentMethod; // CARD, UPI, CASH_ON_DELIVERY

    @Column(nullable = false, unique = true, length = 80)
    private String transactionId;

    @Column(nullable = false, length = 30)
    private String status; // SUCCESS, PENDING, FAILED, REFUNDED

    @Column(length = 255)
    private String failureReason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
