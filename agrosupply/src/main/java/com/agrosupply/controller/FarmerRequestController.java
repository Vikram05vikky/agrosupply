package com.agrosupply.controller;

import com.agrosupply.dto.request.FarmerRequestRequest;
import com.agrosupply.dto.response.FarmerRequestResponse;
import com.agrosupply.enums.RequestStatus;
import com.agrosupply.service.FarmerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class FarmerRequestController {

    private final FarmerRequestService farmerRequestService;

    /*
     * POST /api/requests
     * Farmer places request → system auto handles everything:
     *   → Finds best warehouse with enough stock
     *   → Assigns least busy delivery agent
     *   → Deducts inventory
     *   → Request → SCHEDULED instantly
     */
    @PostMapping
    public ResponseEntity<FarmerRequestResponse> createRequest(
            @Valid @RequestBody FarmerRequestRequest request) {
        return ResponseEntity.ok(farmerRequestService.createRequest(request));
    }

    // GET /api/requests — All requests (admin/warehouse view)
    @GetMapping
    public ResponseEntity<List<FarmerRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(farmerRequestService.getAllRequests());
    }

    // GET /api/requests/{id} — Get single request
    @GetMapping("/{id}")
    public ResponseEntity<FarmerRequestResponse> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(farmerRequestService.getRequestById(id));
    }

    // GET /api/requests/farmer/{farmerId} — Farmer views their own requests
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<FarmerRequestResponse>> getRequestsByFarmer(
            @PathVariable Long farmerId) {
        return ResponseEntity.ok(farmerRequestService.getRequestsByFarmer(farmerId));
    }

    // GET /api/requests/agent/{agentId} — Delivery agent views assigned requests
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<FarmerRequestResponse>> getRequestsByAgent(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(farmerRequestService.getRequestsByAgent(agentId));
    }

    // GET /api/requests/filter?status=SCHEDULED — Filter by status
    @GetMapping("/filter")
    public ResponseEntity<List<FarmerRequestResponse>> getRequestsByStatus(
            @RequestParam RequestStatus status) {
        return ResponseEntity.ok(farmerRequestService.getRequestsByStatus(status));
    }

    /*
     * PATCH /api/requests/{id}/cancel
     * Cancel a request
     * SCHEDULED → cancelled + inventory restored
     * DELIVERED → cannot cancel
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<FarmerRequestResponse> cancelRequest(@PathVariable Long id) {
        return ResponseEntity.ok(farmerRequestService.cancelRequest(id));
    }
}