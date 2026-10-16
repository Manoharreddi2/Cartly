package com.cartly.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    private String name;

    private String slug;

    private String description;

    private String imageUrl;

    @Builder.Default
    private boolean active = true;
}
