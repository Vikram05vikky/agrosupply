package com.agrosupply.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FarmerRequestRequest {

    @NotNull(message = "Farmer ID is required")
    private Long farmerId;

    // At least one product must be in the request
    @NotEmpty(message = "Request must have at least one item")
    private List<RequestItemRequest> items;
}