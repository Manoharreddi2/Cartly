package com.cartly.order.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatsResponse {
    private long totalOrders;
    private long pendingOrders;
    private long deliveredOrders;
    private BigDecimal totalRevenue;
}
