package com.agrosupply.controller;

import com.agrosupply.dto.request.StockTransferRequest;
import com.agrosupply.dto.response.StockMovementResponse;
import com.agrosupply.enums.StockMovementStatus;
import com.agrosupply.service.StockMovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    /*
     * POST /api/stock-movements/transfer
     * Warehouse operator requests a transfer to another warehouse
     * Status = PENDING — stock not moved yet
     */
    @PostMapping("/transfer")
    public ResponseEntity<StockMovementResponse> requestTransfer(
            @Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.ok(stockMovementService.requestTransfer(request));
    }

    // GET /api/stock-movements — Get all stock movements
    @GetMapping
    public ResponseEntity<List<StockMovementResponse>> getAllMovements() {
        return ResponseEntity.ok(stockMovementService.getAllMovements());
    }

    // GET /api/stock-movements/filter?status=PENDING — Filter by status
    @GetMapping("/filter")
    public ResponseEntity<List<StockMovementResponse>> getByStatus(
            @RequestParam StockMovementStatus status) {
        return ResponseEntity.ok(stockMovementService.getMovementsByStatus(status));
    }

    // GET /api/stock-movements/incoming/{warehouseId}
    // Destination warehouse operator sees all pending transfer requests coming to their warehouse
    @GetMapping("/incoming/{warehouseId}")
    public ResponseEntity<List<StockMovementResponse>> getIncomingTransfers(
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockMovementService.getIncomingTransfers(warehouseId));
    }

    // GET /api/stock-movements/outgoing/{warehouseId}
    // Source warehouse operator sees all transfers they requested
    @GetMapping("/outgoing/{warehouseId}")
    public ResponseEntity<List<StockMovementResponse>> getOutgoingTransfers(
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockMovementService.getOutgoingTransfers(warehouseId));
    }

    /*
     * PATCH /api/stock-movements/{id}/approve
     * Destination warehouse operator approves the transfer
     * Stock deducted from source, added to destination
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<StockMovementResponse> approveTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementService.approveTransfer(id));
    }

    /*
     * PATCH /api/stock-movements/{id}/reject
     * Destination warehouse operator rejects the transfer
     * No inventory changes made
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<StockMovementResponse> rejectTransfer(@PathVariable Long id) {
        return ResponseEntity.ok(stockMovementService.rejectTransfer(id));
    }
}