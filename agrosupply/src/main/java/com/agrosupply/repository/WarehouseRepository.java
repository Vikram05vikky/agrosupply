package com.agrosupply.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.Warehouse;
import com.agrosupply.enums.WarehouseStatus;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // Get all warehouses by status
    List<Warehouse> findByStatus(WarehouseStatus status);

    // Get warehouse assigned to a specific operator
    Optional<Warehouse> findByOperatorId(Long operatorId);
}