package com.cartly.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCheckRequest {
    // Map of productId -> requiredQuantity
    @NotEmpty(message = "Items map cannot be empty")
    private Map<Long, Integer> items;
}
