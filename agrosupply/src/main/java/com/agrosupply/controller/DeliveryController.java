package com.agrosupply.controller;

import com.agrosupply.dto.response.DeliveryResponse;
import com.agrosupply.enums.DeliveryStatus;
import com.agrosupply.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // GET /api/deliveries — Admin views all deliveries
    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    // GET /api/deliveries/{id} — Get delivery by ID
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    // GET /api/deliveries/agent/{agentId} — Agent views their assigned deliveries
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByAgent(agentId));
    }

    // GET /api/deliveries/filter?status=IN_PROGRESS — Filter by status
    @GetMapping("/filter")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(
            @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    /*
     * PATCH /api/deliveries/{id}/complete
     * Delivery agent marks delivery as completed
     * Triggers:
     *   → DeliveryStatus   : IN_PROGRESS → COMPLETED
     *   → RequestStatus    : SCHEDULED   → DELIVERED
     *   → Invoice          : auto-generated (UNPAID)
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<DeliveryResponse> completeDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.completeDelivery(id));
    }
}