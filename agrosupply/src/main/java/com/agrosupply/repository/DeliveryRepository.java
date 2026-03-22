package com.agrosupply.repository;

import com.agrosupply.entity.Delivery;
import com.agrosupply.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    // Get all deliveries by status
    List<Delivery> findByStatus(DeliveryStatus status);

    // Get delivery by request ID — one request = one delivery
    Optional<Delivery> findByFarmerRequestId(Long requestId);
}