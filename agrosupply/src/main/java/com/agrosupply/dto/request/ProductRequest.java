package com.agrosupply.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;   // e.g. Seeds, Fertilizer, Equipment

    @NotBlank(message = "Unit is required")
    private String unit;       // e.g. kg, litre, piece

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;
}