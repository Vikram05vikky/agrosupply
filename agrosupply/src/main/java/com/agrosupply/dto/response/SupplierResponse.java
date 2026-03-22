package com.agrosupply.dto.response;

import com.agrosupply.enums.SupplierStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierResponse {
    private Long id;
    private String name;
    private String contactInfo;
    private SupplierStatus status;
}