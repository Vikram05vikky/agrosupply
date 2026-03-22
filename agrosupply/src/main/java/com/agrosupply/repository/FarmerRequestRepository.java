package com.agrosupply.repository;

import com.agrosupply.entity.FarmerRequest;
import com.agrosupply.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmerRequestRepository extends JpaRepository<FarmerRequest, Long> {

    List<FarmerRequest> findByFarmerId(Long farmerId);
    List<FarmerRequest> findByStatus(RequestStatus status);
    List<FarmerRequest> findByDeliveryAgentId(Long agentId);
    List<FarmerRequest> findByWarehouseId(Long warehouseId);

    // Count active deliveries for a specific agent
    // Used to find the least busy agent during auto-assignment
    @Query("SELECT COUNT(r) FROM FarmerRequest r WHERE r.deliveryAgent.id = :agentId " +
           "AND r.status IN ('SCHEDULED', 'DELIVERED')")
    Long countActiveDeliveriesByAgent(@Param("agentId") Long agentId);
}