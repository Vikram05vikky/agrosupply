package com.agrosupply.dto.response;

import java.math.BigDecimal;

import com.agrosupply.enums.RequestItemStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productUnit;
    private BigDecimal productPrice;
    private Double quantity;        // Double — supports decimals
    private BigDecimal totalPrice;
    private RequestItemStatus status;
}