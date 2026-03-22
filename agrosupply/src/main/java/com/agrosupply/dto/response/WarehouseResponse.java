package com.agrosupply.dto.response;

import com.agrosupply.enums.WarehouseStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehouseResponse {
    private Long id;
    private String name;
    private String location;
    private Double maxCapacityKg;
    private Double maxCapacityLitre;
    private Integer maxCapacityPiece;
    private Long operatorId;
    private String operatorName;
    private WarehouseStatus status;
}