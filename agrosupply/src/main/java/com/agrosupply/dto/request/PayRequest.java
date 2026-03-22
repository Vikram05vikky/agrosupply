package com.agrosupply.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PayRequest {

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // e.g. CASH, BANK_TRANSFER, MOBILE_MONEY
}