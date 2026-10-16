package com.cartly.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    // Delivery Address
    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Recipient phone is required")
    private String recipientPhone;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    private String zipCode;

    @Builder.Default
    private String country = "United States";

    // Payment details
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CARD, UPI, CASH_ON_DELIVERY

    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private String upiId;

    // Items (optional: if omitted, will fetch and drain from cart)
    private List<OrderItemDto> items;
}
