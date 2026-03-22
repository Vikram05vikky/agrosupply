package com.agrosupply.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agrosupply.dto.response.InventoryResponse;
import com.agrosupply.entity.Inventory;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // Get all inventory across all warehouses — admin view
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get inventory for a specific warehouse — warehouse operator view
    public List<InventoryResponse> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get stock of a specific product in a specific warehouse
    public InventoryResponse getInventoryByWarehouseAndProduct(Long warehouseId, Long productId) {
        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No inventory found for warehouseId: " + warehouseId + " and productId: " + productId));
        return toResponse(inventory);
    }

    // Convert entity → response DTO
    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .warehouseId(inventory.getWarehouse().getId())
                .warehouseName(inventory.getWarehouse().getName())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .productUnit(inventory.getProduct().getUnit())
                .productPrice(inventory.getProduct().getPrice())
                .quantityAvailable(inventory.getQuantityAvailable())
                .lastUpdatedAt(inventory.getLastUpdatedAt())
                .build();
    }
}