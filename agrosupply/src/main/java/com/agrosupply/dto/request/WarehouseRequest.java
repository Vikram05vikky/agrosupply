package com.agrosupply.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WarehouseRequest {

    @NotBlank(message = "Warehouse name is required")
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Kg capacity is required")
    @Positive(message = "Kg capacity must be greater than 0")
    private Double maxCapacityKg;       // max kg storage  (seeds, fertilizers)

    @NotNull(message = "Litre capacity is required")
    @Positive(message = "Litre capacity must be greater than 0")
    private Double maxCapacityLitre;    // max litre storage (pesticides, liquids)

    @NotNull(message = "Piece capacity is required")
    @Positive(message = "Piece capacity must be greater than 0")
    private Integer maxCapacityPiece;   // max piece storage (equipment)
}