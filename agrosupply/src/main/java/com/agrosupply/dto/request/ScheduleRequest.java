package com.agrosupply.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleRequest {

    // Delivery agent to assign to this request
    @NotNull(message = "Delivery agent ID is required")
    private Long deliveryAgentId;
}