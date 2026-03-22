package com.agrosupply.controller;

import com.agrosupply.dto.request.WarehouseRequest;
import com.agrosupply.dto.response.WarehouseResponse;
import com.agrosupply.enums.WarehouseStatus;
import com.agrosupply.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    // POST /api/warehouses — Admin creates a warehouse
    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.createWarehouse(request));
    }

    // GET /api/warehouses — Get all warehouses
    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    // GET /api/warehouses/{id} — Get warehouse by ID
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    // PATCH /api/warehouses/{id}/assign-operator?operatorId=4 — Admin assigns operator
    @PatchMapping("/{id}/assign-operator")
    public ResponseEntity<WarehouseResponse> assignOperator(@PathVariable Long id,
                                                             @RequestParam Long operatorId) {
        return ResponseEntity.ok(warehouseService.assignOperator(id, operatorId));
    }

    // PATCH /api/warehouses/{id}/status?status=INACTIVE — Activate or deactivate
    @PatchMapping("/{id}/status")
    public ResponseEntity<WarehouseResponse> updateStatus(@PathVariable Long id,
                                                           @RequestParam WarehouseStatus status) {
        return ResponseEntity.ok(warehouseService.updateWarehouseStatus(id, status));
    }
}