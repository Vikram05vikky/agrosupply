package com.agrosupply.util;

import org.springframework.stereotype.Component;

import com.agrosupply.entity.Warehouse;
import com.agrosupply.exception.CapacityExceededException;
import com.agrosupply.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CapacityValidator {

    private final InventoryRepository inventoryRepository;

    public void validate(Warehouse warehouse, String productUnit, Double incomingQuantity) {
        switch (productUnit.toLowerCase()) {

            case "kg" -> {
                Double current = inventoryRepository.getTotalKgByWarehouseId(warehouse.getId());
                double after = current + incomingQuantity;
                if (after > warehouse.getMaxCapacityKg()) {
                    throw new CapacityExceededException(
                            "Warehouse kg capacity exceeded! " +
                            "Warehouse: " + warehouse.getName() +
                            " | Max: " + warehouse.getMaxCapacityKg() + "kg" +
                            " | Current: " + current + "kg" +
                            " | Incoming: " + incomingQuantity + "kg" +
                            " | Exceeds by: " + (after - warehouse.getMaxCapacityKg()) + "kg"
                    );
                }
            }

            case "litre" -> {
                Double current = inventoryRepository.getTotalLitreByWarehouseId(warehouse.getId());
                double after = current + incomingQuantity;
                if (after > warehouse.getMaxCapacityLitre()) {
                    throw new CapacityExceededException(
                            "Warehouse litre capacity exceeded! " +
                            "Warehouse: " + warehouse.getName() +
                            " | Max: " + warehouse.getMaxCapacityLitre() + "L" +
                            " | Current: " + current + "L" +
                            " | Incoming: " + incomingQuantity + "L" +
                            " | Exceeds by: " + (after - warehouse.getMaxCapacityLitre()) + "L"
                    );
                }
            }

            case "piece" -> {
                Double current = inventoryRepository.getTotalPieceByWarehouseId(warehouse.getId());
                double after = current + incomingQuantity;
                if (after > warehouse.getMaxCapacityPiece()) {
                    throw new CapacityExceededException(
                            "Warehouse piece capacity exceeded! " +
                            "Warehouse: " + warehouse.getName() +
                            " | Max: " + warehouse.getMaxCapacityPiece() + " pieces" +
                            " | Current: " + current + " pieces" +
                            " | Incoming: " + incomingQuantity + " pieces" +
                            " | Exceeds by: " + (after - warehouse.getMaxCapacityPiece()) + " pieces"
                    );
                }
            }

            default -> throw new CapacityExceededException(
                    "Unknown product unit: " + productUnit + ". Supported: kg, litre, piece"
            );
        }
    }
}