package com.agrosupply.dto.response;

import java.time.LocalDateTime;

import com.agrosupply.enums.StockMovementStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockMovementResponse {
    private Long id;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private Long productId;
    private String productName;
    private Double quantity;            // Double — supports decimals
    private StockMovementStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime resolvedAt;
}