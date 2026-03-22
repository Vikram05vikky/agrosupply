package com.agrosupply.dto.response;

import java.math.BigDecimal;

import com.agrosupply.enums.ProductStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String category;
    private String unit;
    private BigDecimal price;
    private ProductStatus status;
}