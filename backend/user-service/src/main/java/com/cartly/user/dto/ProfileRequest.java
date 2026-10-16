package com.cartly.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {
    @NotBlank(message = "Name cannot be empty")
    private String name;
    private String phone;
    private String avatarUrl;
}
