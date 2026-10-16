package com.cartly.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private boolean isDefault;
    private LocalDateTime createdAt;
}
