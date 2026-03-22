package com.agrosupply.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosupply.dto.response.DeliveryResponse;
import com.agrosupply.dto.response.RequestItemResponse;
import com.agrosupply.entity.Delivery;
import com.agrosupply.entity.FarmerRequest;
import com.agrosupply.entity.Invoice;
import com.agrosupply.entity.RequestItem;
import com.agrosupply.enums.DeliveryStatus;
import com.agrosupply.enums.InvoiceStatus;
import com.agrosupply.enums.RequestStatus;
import com.agrosupply.exception.BadRequestException;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.DeliveryRepository;
import com.agrosupply.repository.FarmerRequestRepository;
import com.agrosupply.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final FarmerRequestRepository farmerRequestRepository;
    private final InvoiceRepository invoiceRepository;

    // Auto-called from FarmerRequestService when request is created
    public Delivery createDelivery(FarmerRequest request) {
        Delivery delivery = Delivery.builder()
                .farmerRequest(request)
                .status(DeliveryStatus.IN_PROGRESS)
                .build();
        return deliveryRepository.save(delivery);
    }

    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public DeliveryResponse getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
        return toResponse(delivery);
    }

    public List<DeliveryResponse> getDeliveriesByAgent(Long agentId) {
        return farmerRequestRepository.findByDeliveryAgentId(agentId)
                .stream()
                .map(request -> deliveryRepository
                        .findByFarmerRequestId(request.getId())
                        .orElse(null))
                .filter(delivery -> delivery != null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponse> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /*
     * Delivery agent marks delivery as COMPLETED
     * Triggers:
     *   1. Delivery    → COMPLETED
     *   2. Request     → DELIVERED
     *   3. Invoice     → auto-generated (UNPAID)
     */
    @Transactional
    public DeliveryResponse completeDelivery(Long deliveryId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + deliveryId));

        if (delivery.getStatus() == DeliveryStatus.COMPLETED) {
            throw new BadRequestException("Delivery is already completed");
        }

        FarmerRequest request = delivery.getFarmerRequest();

        if (request.getStatus() != RequestStatus.SCHEDULED) {
            throw new BadRequestException("Request is not in SCHEDULED status");
        }

        // Trigger 1 — Complete delivery
        delivery.setStatus(DeliveryStatus.COMPLETED);
        delivery.setDeliveredAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        // Trigger 2 — Mark request delivered
        request.setStatus(RequestStatus.DELIVERED);
        farmerRequestRepository.save(request);

        // Trigger 3 — Auto-generate invoice
        BigDecimal totalAmount = request.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice invoice = Invoice.builder()
                .farmer(request.getFarmer())
                .farmerRequest(request)
                .totalAmount(totalAmount)
                .status(InvoiceStatus.UNPAID)
                .build();

        invoiceRepository.save(invoice);

        return toResponse(delivery);
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        FarmerRequest request = delivery.getFarmerRequest();

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

        return DeliveryResponse.builder()
                .id(delivery.getId())
                .requestId(request.getId())
                .farmerId(request.getFarmer().getId())
                .farmerName(request.getFarmer().getName())
                .farmerLocation(request.getFarmer().getLocation())
                .warehouseId(request.getWarehouse().getId())
                .warehouseName(request.getWarehouse().getName())
                .deliveryAgentId(request.getDeliveryAgent().getId())
                .deliveryAgentName(request.getDeliveryAgent().getName())
                .items(itemResponses)
                .status(delivery.getStatus())
                .scheduledAt(request.getScheduledAt())
                .deliveredAt(delivery.getDeliveredAt())
                .build();
    }
}