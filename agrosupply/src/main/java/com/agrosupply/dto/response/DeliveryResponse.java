package com.agrosupply.dto.response;

import com.agrosupply.enums.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DeliveryResponse {
    private Long id;

    // Request details
    private Long requestId;
    private Long farmerId;
    private String farmerName;
    private String farmerLocation;

    // Warehouse details
    private Long warehouseId;
    private String warehouseName;

    // Delivery agent details
    private Long deliveryAgentId;
    private String deliveryAgentName;

    // Items to deliver
    private List<RequestItemResponse> items;

    private DeliveryStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime deliveredAt;
}