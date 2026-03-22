package com.agrosupply.repository;

import com.agrosupply.entity.StockMovement;
import com.agrosupply.enums.StockMovementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    // Get all movements by status (PENDING / APPROVED / REJECTED)
    List<StockMovement> findByStatus(StockMovementStatus status);

    // Get all movements coming from a specific warehouse
    List<StockMovement> findByFromWarehouseId(Long warehouseId);

    // Get all movements going to a specific warehouse
    // Used by destination warehouse operator to see pending approvals
    List<StockMovement> findByToWarehouseId(Long warehouseId);
}