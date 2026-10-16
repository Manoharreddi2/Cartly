package com.cartly.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReserveRequest {
    // Map of productId -> quantityToReserveOrDeduct
    @NotEmpty(message = "Items map cannot be empty")
    private Map<Long, Integer> items;
}
