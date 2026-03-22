package com.agrosupply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agrosupply.entity.Product;
import com.agrosupply.enums.ProductStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all products by status — farmers only see ACTIVE products
    List<Product> findByStatus(ProductStatus status);

    // Get products by category (e.g. Seeds, Fertilizer, Equipment)
    List<Product> findByCategory(String category);
}