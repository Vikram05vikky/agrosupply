package com.agrosupply.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.agrosupply.enums.RequestStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FarmerRequestResponse {
    private Long id;

    // Farmer details
    private Long farmerId;
    private String farmerName;

    // Set when warehouse operator allocates stock
    private Long warehouseId;
    private String warehouseName;

    // Set when delivery agent is assigned
    private Long deliveryAgentId;
    private String deliveryAgentName;

    private List<RequestItemResponse> items;

    // PENDING → ALLOCATED → SCHEDULED → DELIVERED → CANCELLED
    private RequestStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime scheduledAt;
}