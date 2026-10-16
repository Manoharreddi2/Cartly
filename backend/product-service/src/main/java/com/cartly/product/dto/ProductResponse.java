package com.cartly.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer discount;
    private BigDecimal discountedPrice;
    private String category;
    private String brand;
    private Integer stock;
    private String imageUrl;
    private Double rating;
    private Integer reviewCount;
    private boolean isFeatured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
