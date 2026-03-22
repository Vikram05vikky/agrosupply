package com.agrosupply.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agrosupply.dto.request.WarehouseRequest;
import com.agrosupply.dto.response.WarehouseResponse;
import com.agrosupply.entity.User;
import com.agrosupply.entity.Warehouse;
import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;
import com.agrosupply.enums.WarehouseStatus;
import com.agrosupply.exception.BadRequestException;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.UserRepository;
import com.agrosupply.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    // Admin creates a new warehouse with separate capacity per unit type
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .maxCapacityKg(request.getMaxCapacityKg())
                .maxCapacityLitre(request.getMaxCapacityLitre())
                .maxCapacityPiece(request.getMaxCapacityPiece())
                .status(WarehouseStatus.ACTIVE)
                .operator(null)     // no operator assigned yet
                .build();

        return toResponse(warehouseRepository.save(warehouse));
    }

    // Get all warehouses
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get warehouse by ID
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));
        return toResponse(warehouse);
    }

    /*
     * Admin assigns a warehouse operator to a warehouse
     * Validations:
     * 1. User must exist
     * 2. User must have WAREHOUSE role
     * 3. User must be ACTIVE (admin approved)
     */
    public WarehouseResponse assignOperator(Long warehouseId, Long operatorId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + warehouseId));

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + operatorId));

        if (operator.getRole() != Role.WAREHOUSE) {
            throw new BadRequestException("User is not a warehouse operator");
        }

        if (operator.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException(
                    "Cannot assign operator — user account is not active. " +
                    "Current status: " + operator.getStatus()
            );
        }

        warehouse.setOperator(operator);
        return toResponse(warehouseRepository.save(warehouse));
    }

    // Activate or deactivate a warehouse
    public WarehouseResponse updateWarehouseStatus(Long id, WarehouseStatus status) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + id));

        warehouse.setStatus(status);
        return toResponse(warehouseRepository.save(warehouse));
    }

    // Convert entity → response DTO
    public WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .maxCapacityKg(warehouse.getMaxCapacityKg())
                .maxCapacityLitre(warehouse.getMaxCapacityLitre())
                .maxCapacityPiece(warehouse.getMaxCapacityPiece())
                .operatorId(warehouse.getOperator() != null ? warehouse.getOperator().getId() : null)
                .operatorName(warehouse.getOperator() != null ? warehouse.getOperator().getName() : null)
                .status(warehouse.getStatus())
                .build();
    }
}