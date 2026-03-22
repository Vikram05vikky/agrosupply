package com.agrosupply.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {

    @NotBlank(message = "Supplier name is required")
    private String name;

    @NotBlank(message = "Contact info is required")
    private String contactInfo;
}