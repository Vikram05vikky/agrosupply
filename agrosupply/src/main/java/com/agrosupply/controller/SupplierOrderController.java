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

import com.agrosupply.dto.request.SupplierOrderRequest;
import com.agrosupply.dto.response.SupplierOrderResponse;
import com.agrosupply.enums.OrderStatus;
import com.agrosupply.service.SupplierOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/supplier-orders")
@RequiredArgsConstructor
public class SupplierOrderController {

    private final SupplierOrderService supplierOrderService;

    // POST /api/supplier-orders — Procurement officer places a new order
    @PostMapping
    public ResponseEntity<SupplierOrderResponse> createOrder(@Valid @RequestBody SupplierOrderRequest request) {
        return ResponseEntity.ok(supplierOrderService.createOrder(request));
    }

    // GET /api/supplier-orders — Get all orders
    @GetMapping
    public ResponseEntity<List<SupplierOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(supplierOrderService.getAllOrders());
    }

    // GET /api/supplier-orders/{id} — Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<SupplierOrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierOrderService.getOrderById(id));
    }

    // GET /api/supplier-orders/officer/{officerId} — Get orders by procurement officer
    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<SupplierOrderResponse>> getOrdersByOfficer(@PathVariable Long officerId) {
        return ResponseEntity.ok(supplierOrderService.getOrdersByOfficer(officerId));
    }

    // GET /api/supplier-orders/filter?status=PENDING — Filter by status
    @GetMapping("/filter")
    public ResponseEntity<List<SupplierOrderResponse>> getOrdersByStatus(@RequestParam OrderStatus status) {
        return ResponseEntity.ok(supplierOrderService.getOrdersByStatus(status));
    }

    /*
     * PATCH /api/supplier-orders/{id}/deliver?warehouseId=1
     * Procurement officer marks order as DELIVERED
     * Requires warehouseId — stock is added to that warehouse's inventory
     * Triggers auto inventory update for each item in the order
     */
    @PatchMapping("/{id}/deliver")
    public ResponseEntity<SupplierOrderResponse> markDelivered(@PathVariable Long id,
                                                                @RequestParam Long warehouseId) {
        return ResponseEntity.ok(supplierOrderService.markDelivered(id, warehouseId));
    }

    // PATCH /api/supplier-orders/{id}/cancel — Cancel a PENDING order
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SupplierOrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(supplierOrderService.cancelOrder(id));
    }
}