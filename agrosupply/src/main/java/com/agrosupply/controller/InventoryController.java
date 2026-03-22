package com.agrosupply.controller;

import com.agrosupply.dto.response.InventoryResponse;
import com.agrosupply.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // GET /api/inventory — Admin views all inventory across all warehouses
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    // GET /api/inventory/warehouse/{warehouseId} — Warehouse operator views their stock
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryResponse>> getByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouse(warehouseId));
    }

    // GET /api/inventory/warehouse/{warehouseId}/product/{productId} — Check specific product stock
    @GetMapping("/warehouse/{warehouseId}/product/{productId}")
    public ResponseEntity<InventoryResponse> getByWarehouseAndProduct(@PathVariable Long warehouseId,
                                                                       @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouseAndProduct(warehouseId, productId));
    }
}