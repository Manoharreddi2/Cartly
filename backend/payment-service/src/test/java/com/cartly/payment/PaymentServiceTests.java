package com.cartly.payment;

import com.cartly.payment.dto.PaymentRequest;
import com.cartly.payment.dto.PaymentResponse;
import com.cartly.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PaymentServiceTests {

    @Autowired
    private PaymentService paymentService;

    @Test
    void testProcessPaymentCardSuccess() {
        PaymentRequest request = PaymentRequest.builder()
                .orderId(101L)
                .userId(1L)
                .amount(new BigDecimal("199.99"))
                .paymentMethod("CARD")
                .cardNumber("4111111111111111")
                .build();

        PaymentResponse response = paymentService.processPayment(request);
        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getTransactionId().startsWith("TXN-"));
    }

    @Test
    void testProcessPaymentCOD() {
        PaymentRequest request = PaymentRequest.builder()
                .orderId(102L)
                .userId(1L)
                .amount(new BigDecimal("89.50"))
                .paymentMethod("CASH_ON_DELIVERY")
                .build();

        PaymentResponse response = paymentService.processPayment(request);
        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void testRefundPayment() {
        PaymentRequest request = PaymentRequest.builder()
                .orderId(103L)
                .userId(1L)
                .amount(new BigDecimal("50.00"))
                .paymentMethod("UPI")
                .upiId("user@okhdfcbank")
                .build();

        paymentService.processPayment(request);

        PaymentResponse refunded = paymentService.refundPayment(103L);
        assertEquals("REFUNDED", refunded.getStatus());
    }
}
