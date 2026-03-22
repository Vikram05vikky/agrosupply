package com.agrosupply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get all items belonging to a specific supplier order
    List<OrderItem> findBySupplierOrderId(Long supplierOrderId);
}