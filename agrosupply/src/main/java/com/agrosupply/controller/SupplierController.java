package com.agrosupply.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosupply.dto.request.SupplierRequest;
import com.agrosupply.dto.response.SupplierResponse;
import com.agrosupply.enums.SupplierStatus;
import com.agrosupply.service.SupplierService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // POST /api/suppliers — Admin creates a new supplier
    @PostMapping
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.createSupplier(request));
    }

    // GET /api/suppliers — Get all suppliers
    @GetMapping
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    // GET /api/suppliers/{id} — Get supplier by ID
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    // PATCH /api/suppliers/{id}/status?status=INACTIVE — Activate or deactivate
    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplierResponse> updateStatus(@PathVariable Long id,
                                                          @RequestParam SupplierStatus status) {
        return ResponseEntity.ok(supplierService.updateSupplierStatus(id, status));
    }
}