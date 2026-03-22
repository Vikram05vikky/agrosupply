package com.agrosupply.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosupply.dto.request.OrderItemRequest;
import com.agrosupply.dto.request.SupplierOrderRequest;
import com.agrosupply.dto.response.OrderItemResponse;
import com.agrosupply.dto.response.SupplierOrderResponse;
import com.agrosupply.entity.Inventory;
import com.agrosupply.entity.OrderItem;
import com.agrosupply.entity.Product;
import com.agrosupply.entity.Supplier;
import com.agrosupply.entity.SupplierOrder;
import com.agrosupply.entity.User;
import com.agrosupply.entity.Warehouse;
import com.agrosupply.enums.OrderStatus;
import com.agrosupply.exception.BadRequestException;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.InventoryRepository;
import com.agrosupply.repository.ProductRepository;
import com.agrosupply.repository.SupplierOrderRepository;
import com.agrosupply.repository.SupplierRepository;
import com.agrosupply.repository.UserRepository;
import com.agrosupply.repository.WarehouseRepository;
import com.agrosupply.util.CapacityValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierOrderService {

    private final SupplierOrderRepository supplierOrderRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final CapacityValidator capacityValidator;

    @Transactional
    public SupplierOrderResponse createOrder(SupplierOrderRequest request) {

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));

        User procurementOfficer = userRepository.findById(request.getProcurementOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getProcurementOfficerId()));

        // Save order header first
        SupplierOrder order = SupplierOrder.builder()
                .supplier(supplier)
                .procurementOfficer(procurementOfficer)
                .status(OrderStatus.PENDING)
                .build();

        order = supplierOrderRepository.save(order);

        // Build items separately
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            OrderItem item = OrderItem.builder()
                    .supplierOrder(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .build();

            items.add(item);
        }

        order.setItems(items);
        return toResponse(supplierOrderRepository.save(order));
    }

    public List<SupplierOrderResponse> getAllOrders() {
        return supplierOrderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SupplierOrderResponse getOrderById(Long id) {
        SupplierOrder order = supplierOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + id));
        return toResponse(order);
    }

    public List<SupplierOrderResponse> getOrdersByOfficer(Long officerId) {
        return supplierOrderRepository.findByProcurementOfficerId(officerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<SupplierOrderResponse> getOrdersByStatus(OrderStatus status) {
        return supplierOrderRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupplierOrderResponse markDelivered(Long orderId, Long warehouseId) {

        SupplierOrder order = supplierOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be marked as delivered");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + warehouseId));

        // Validate capacity per item unit type
        for (OrderItem item : order.getItems()) {
            capacityValidator.validate(warehouse, item.getProduct().getUnit(), item.getQuantity());
        }

        // Update inventory for each item
        for (OrderItem item : order.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByWarehouseIdAndProductId(warehouse.getId(), item.getProduct().getId())
                    .orElse(null);

            if (inventory != null) {
                inventory.setQuantityAvailable(inventory.getQuantityAvailable() + item.getQuantity());
                inventory.setLastUpdatedAt(LocalDateTime.now());
                inventoryRepository.save(inventory);
            } else {
                Inventory newInventory = Inventory.builder()
                        .warehouse(warehouse)
                        .product(item.getProduct())
                        .quantityAvailable(item.getQuantity())
                        .lastUpdatedAt(LocalDateTime.now())
                        .build();
                inventoryRepository.save(newInventory);
            }
        }

        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        return toResponse(supplierOrderRepository.save(order));
    }

    @Transactional
    public SupplierOrderResponse cancelOrder(Long orderId) {
        SupplierOrder order = supplierOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only PENDING orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(supplierOrderRepository.save(order));
    }

    private SupplierOrderResponse toResponse(SupplierOrder order) {
        List<OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            BigDecimal pricePerUnit = item.getProduct().getPrice();
            BigDecimal totalPrice = pricePerUnit.multiply(BigDecimal.valueOf(item.getQuantity()));
            itemResponses.add(OrderItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .unit(item.getProduct().getUnit())
                    .quantity(item.getQuantity())
                    .pricePerUnit(pricePerUnit)
                    .totalPrice(totalPrice)
                    .build());
        }

        return SupplierOrderResponse.builder()
                .id(order.getId())
                .supplierId(order.getSupplier().getId())
                .supplierName(order.getSupplier().getName())
                .procurementOfficerId(order.getProcurementOfficer().getId())
                .procurementOfficerName(order.getProcurementOfficer().getName())
                .items(itemResponses)
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .deliveredAt(order.getDeliveredAt())
                .build();
    }
}