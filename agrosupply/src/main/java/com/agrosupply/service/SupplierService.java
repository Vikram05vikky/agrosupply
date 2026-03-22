package com.agrosupply.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agrosupply.dto.request.SupplierRequest;
import com.agrosupply.dto.response.SupplierResponse;
import com.agrosupply.entity.Supplier;
import com.agrosupply.enums.SupplierStatus;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    // Create a new supplier
    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .name(request.getName())
                .contactInfo(request.getContactInfo())
                .status(SupplierStatus.ACTIVE)  // always starts as ACTIVE
                .build();

        return toResponse(supplierRepository.save(supplier));
    }

    // Get all suppliers
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get supplier by ID
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return toResponse(supplier);
    }

    // Activate or deactivate a supplier
    public SupplierResponse updateSupplierStatus(Long id, SupplierStatus status) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        supplier.setStatus(status);
        return toResponse(supplierRepository.save(supplier));
    }

    // Convert entity → response DTO
    private SupplierResponse toResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactInfo(supplier.getContactInfo())
                .status(supplier.getStatus())
                .build();
    }
}