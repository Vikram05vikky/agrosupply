package com.agrosupply.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosupply.dto.request.StockTransferRequest;
import com.agrosupply.dto.response.StockMovementResponse;
import com.agrosupply.entity.Inventory;
import com.agrosupply.entity.Product;
import com.agrosupply.entity.StockMovement;
import com.agrosupply.entity.Warehouse;
import com.agrosupply.enums.StockMovementStatus;
import com.agrosupply.exception.BadRequestException;
import com.agrosupply.exception.InsufficientStockException;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.InventoryRepository;
import com.agrosupply.repository.ProductRepository;
import com.agrosupply.repository.StockMovementRepository;
import com.agrosupply.repository.WarehouseRepository;
import com.agrosupply.util.CapacityValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CapacityValidator capacityValidator;

    @Transactional
    public StockMovementResponse requestTransfer(StockTransferRequest request) {

        Warehouse fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Source warehouse not found"));

        Warehouse toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination warehouse not found"));

        if (fromWarehouse.getId().equals(toWarehouse.getId())) {
            throw new BadRequestException("Source and destination warehouse cannot be the same");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Inventory sourceInventory = inventoryRepository
                .findByWarehouseIdAndProductId(fromWarehouse.getId(), product.getId())
                .orElseThrow(() -> new InsufficientStockException("Product not found in source warehouse"));

        if (sourceInventory.getQuantityAvailable() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock in source warehouse. " +
                    "Available: " + sourceInventory.getQuantityAvailable() +
                    " | Requested: " + request.getQuantity()
            );
        }

        capacityValidator.validate(toWarehouse, product.getUnit(), request.getQuantity());

        StockMovement movement = StockMovement.builder()
                .fromWarehouse(fromWarehouse)
                .toWarehouse(toWarehouse)
                .product(product)
                .quantity(request.getQuantity())
                .status(StockMovementStatus.PENDING)
                .build();

        return toResponse(stockMovementRepository.save(movement));
    }

    @Transactional
    public StockMovementResponse approveTransfer(Long movementId) {

        StockMovement movement = stockMovementRepository.findById(movementId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with id: " + movementId));

        if (movement.getStatus() != StockMovementStatus.PENDING) {
            throw new BadRequestException("Only PENDING transfers can be approved");
        }

        Inventory sourceInventory = inventoryRepository
                .findByWarehouseIdAndProductId(
                        movement.getFromWarehouse().getId(),
                        movement.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Source inventory not found"));

        if (sourceInventory.getQuantityAvailable() < movement.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock at time of approval. Available: " +
                    sourceInventory.getQuantityAvailable()
            );
        }

        capacityValidator.validate(
                movement.getToWarehouse(),
                movement.getProduct().getUnit(),
                movement.getQuantity()
        );

        sourceInventory.setQuantityAvailable(sourceInventory.getQuantityAvailable() - movement.getQuantity());
        sourceInventory.setLastUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(sourceInventory);

        Inventory destinationInventory = inventoryRepository
                .findByWarehouseIdAndProductId(
                        movement.getToWarehouse().getId(),
                        movement.getProduct().getId())
                .orElse(null);

        if (destinationInventory != null) {
            destinationInventory.setQuantityAvailable(
                    destinationInventory.getQuantityAvailable() + movement.getQuantity());
            destinationInventory.setLastUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(destinationInventory);
        } else {
            Inventory newInventory = Inventory.builder()
                    .warehouse(movement.getToWarehouse())
                    .product(movement.getProduct())
                    .quantityAvailable(movement.getQuantity())
                    .lastUpdatedAt(LocalDateTime.now())
                    .build();
            inventoryRepository.save(newInventory);
        }

        movement.setStatus(StockMovementStatus.APPROVED);
        movement.setResolvedAt(LocalDateTime.now());
        return toResponse(stockMovementRepository.save(movement));
    }

    @Transactional
    public StockMovementResponse rejectTransfer(Long movementId) {
        StockMovement movement = stockMovementRepository.findById(movementId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement not found with id: " + movementId));

        if (movement.getStatus() != StockMovementStatus.PENDING) {
            throw new BadRequestException("Only PENDING transfers can be rejected");
        }

        movement.setStatus(StockMovementStatus.REJECTED);
        movement.setResolvedAt(LocalDateTime.now());
        return toResponse(stockMovementRepository.save(movement));
    }

    public List<StockMovementResponse> getAllMovements() {
        return stockMovementRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StockMovementResponse> getMovementsByStatus(StockMovementStatus status) {
        return stockMovementRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StockMovementResponse> getIncomingTransfers(Long warehouseId) {
        return stockMovementRepository.findByToWarehouseId(warehouseId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<StockMovementResponse> getOutgoingTransfers(Long warehouseId) {
        return stockMovementRepository.findByFromWarehouseId(warehouseId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private StockMovementResponse toResponse(StockMovement movement) {
        return StockMovementResponse.builder()
                .id(movement.getId())
                .fromWarehouseId(movement.getFromWarehouse().getId())
                .fromWarehouseName(movement.getFromWarehouse().getName())
                .toWarehouseId(movement.getToWarehouse().getId())
                .toWarehouseName(movement.getToWarehouse().getName())
                .productId(movement.getProduct().getId())
                .productName(movement.getProduct().getName())
                .quantity(movement.getQuantity())
                .status(movement.getStatus())
                .requestedAt(movement.getRequestedAt())
                .resolvedAt(movement.getResolvedAt())
                .build();
    }
}