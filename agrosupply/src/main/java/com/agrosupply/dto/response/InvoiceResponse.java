package com.agrosupply.dto.response;

import com.agrosupply.enums.InvoiceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceResponse {
    private Long id;
    private Long farmerId;
    private String farmerName;
    private Long requestId;

    // Line items with prices
    private List<RequestItemResponse> items;

    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime issuedAt;

    // Set when farmer pays
    private LocalDateTime paidAt;
    private String paymentMethod;
}