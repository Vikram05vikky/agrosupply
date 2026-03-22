package com.agrosupply.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SupplierOrderRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Procurement officer ID is required")
    private Long procurementOfficerId;

    // At least one product must be in the order
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;
}