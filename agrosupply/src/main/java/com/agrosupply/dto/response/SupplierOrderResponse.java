package com.agrosupply.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.agrosupply.enums.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierOrderResponse {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private Long procurementOfficerId;
    private String procurementOfficerName;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private LocalDateTime deliveredAt;
}