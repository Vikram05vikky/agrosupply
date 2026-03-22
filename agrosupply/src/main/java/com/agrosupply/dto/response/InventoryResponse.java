package com.agrosupply.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private Long productId;
    private String productName;
    private String productUnit;
    private BigDecimal productPrice;
    private Double quantityAvailable;   // Double — supports decimals
    private LocalDateTime lastUpdatedAt;
}