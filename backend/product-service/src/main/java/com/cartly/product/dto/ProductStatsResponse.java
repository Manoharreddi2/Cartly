package com.cartly.product.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatsResponse {
    private long totalProducts;
    private long lowStockProducts;
    private long categoriesCount;
}
