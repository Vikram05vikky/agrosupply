package com.agrosupply.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class StockTransferRequest {

    @NotNull(message = "Source warehouse ID is required")
    private Long fromWarehouseId;

    @NotNull(message = "Destination warehouse ID is required")
    private Long toWarehouseId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Double quantity;
}