package com.agrosupply.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AllocateRequest {

    // Which warehouse to allocate stock from
    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;
}