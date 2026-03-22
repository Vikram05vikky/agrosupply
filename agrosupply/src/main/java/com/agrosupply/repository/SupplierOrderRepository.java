package com.agrosupply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.SupplierOrder;
import com.agrosupply.enums.OrderStatus;

@Repository
public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {

    // Get all orders by status (PENDING / DELIVERED / CANCELLED)
    List<SupplierOrder> findByStatus(OrderStatus status);

    // Get all orders placed by a specific procurement officer
    List<SupplierOrder> findByProcurementOfficerId(Long procurementOfficerId);

    // Get all orders for a specific supplier
    List<SupplierOrder> findBySupplierId(Long supplierId);
}