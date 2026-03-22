package com.agrosupply.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Get all inventory rows for a specific warehouse
    List<Inventory> findByWarehouseId(Long warehouseId);

    // Get specific product stock in a specific warehouse
    Optional<Inventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    // Total kg stock in a warehouse (seeds, fertilizers)
    @Query("SELECT COALESCE(SUM(i.quantityAvailable), 0) FROM Inventory i " +
           "WHERE i.warehouse.id = :warehouseId AND i.product.unit = 'kg'")
    Double getTotalKgByWarehouseId(@Param("warehouseId") Long warehouseId);

    // Total litre stock in a warehouse (pesticides, liquids)
    @Query("SELECT COALESCE(SUM(i.quantityAvailable), 0) FROM Inventory i " +
           "WHERE i.warehouse.id = :warehouseId AND i.product.unit = 'litre'")
    Double getTotalLitreByWarehouseId(@Param("warehouseId") Long warehouseId);

    // Total piece stock in a warehouse (equipment)
    @Query("SELECT COALESCE(SUM(i.quantityAvailable), 0) FROM Inventory i " +
           "WHERE i.warehouse.id = :warehouseId AND i.product.unit = 'piece'")
    Double getTotalPieceByWarehouseId(@Param("warehouseId") Long warehouseId);
}