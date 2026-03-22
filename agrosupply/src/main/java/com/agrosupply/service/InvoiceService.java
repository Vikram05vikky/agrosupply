package com.agrosupply.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosupply.dto.request.PayRequest;
import com.agrosupply.dto.response.InvoiceResponse;
import com.agrosupply.dto.response.RequestItemResponse;
import com.agrosupply.entity.FarmerRequest;
import com.agrosupply.entity.Invoice;
import com.agrosupply.entity.RequestItem;
import com.agrosupply.enums.InvoiceStatus;
import com.agrosupply.exception.BadRequestException;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return toResponse(invoice);
    }

    public List<InvoiceResponse> getInvoicesByFarmer(Long farmerId) {
        return invoiceRepository.findByFarmerId(farmerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public InvoiceResponse payInvoice(Long invoiceId, PayRequest payRequest) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("Invoice is already paid");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoice.setPaymentMethod(payRequest.getPaymentMethod());

        return toResponse(invoiceRepository.save(invoice));
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        FarmerRequest request = invoice.getFarmerRequest();

        List<RequestItemResponse> itemResponses = new ArrayList<>();
        for (RequestItem item : request.getItems()) {
            BigDecimal price = item.getProduct().getPrice();
            BigDecimal total = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            itemResponses.add(RequestItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productUnit(item.getProduct().getUnit())
                    .productPrice(price)
                    .quantity(item.getQuantity())
                    .totalPrice(total)
                    .status(item.getStatus())
                    .build());
        }

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .farmerId(invoice.getFarmer().getId())
                .farmerName(invoice.getFarmer().getName())
                .requestId(request.getId())
                .items(itemResponses)
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .issuedAt(invoice.getIssuedAt())
                .paidAt(invoice.getPaidAt())
                .paymentMethod(invoice.getPaymentMethod())
                .build();
    }
}