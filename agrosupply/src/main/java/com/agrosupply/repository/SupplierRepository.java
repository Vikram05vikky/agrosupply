package com.agrosupply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.Supplier;
import com.agrosupply.enums.SupplierStatus;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    // Get all suppliers by status (ACTIVE / INACTIVE)
    List<Supplier> findByStatus(SupplierStatus status);
}