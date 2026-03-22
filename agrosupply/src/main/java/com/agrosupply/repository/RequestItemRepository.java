package com.agrosupply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.RequestItem;

@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {

    // Get all items belonging to a specific farmer request
    List<RequestItem> findByFarmerRequestId(Long farmerRequestId);
}