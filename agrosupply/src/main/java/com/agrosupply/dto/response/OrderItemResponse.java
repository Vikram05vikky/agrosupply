package com.agrosupply.dto.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String unit;
    private Double quantity;        // Double — supports decimals
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;
}