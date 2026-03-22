package com.agrosupply.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrosupply.dto.request.FarmerRequestRequest;
import com.agrosupply.dto.request.RequestItemRequest;
import com.agrosupply.dto.response.FarmerRequestResponse;
import com.agrosupply.dto.response.RequestItemResponse;
import com.agrosupply.entity.FarmerRequest;
import com.agrosupply.entity.Inventory;
import com.agrosupply.entity.Product;
import com.agrosupply.entity.RequestItem;
import com.agrosupply.entity.User;
import com.agrosupply.entity.Warehouse;
import com.agrosupply.enums.ProductStatus;
import com.agrosupply.enums.RequestItemStatus;
import com.agrosupply.enums.RequestStatus;
import com.agrosupply.enums.Role;
import com.agrosupply.enums.UserStatus;
import com.agrosupply.enums.WarehouseStatus;
import com.agrosupply.exception.BadRequestException;
import com.agrosupply.exception.InsufficientStockException;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.FarmerRequestRepository;
import com.agrosupply.repository.InventoryRepository;
import com.agrosupply.repository.ProductRepository;
import com.agrosupply.repository.RequestItemRepository;
import com.agrosupply.repository.UserRepository;
import com.agrosupply.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FarmerRequestService {

    private final FarmerRequestRepository farmerRequestRepository;
    private final RequestItemRepository requestItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final DeliveryService deliveryService;

    @Transactional
    public FarmerRequestResponse createRequest(FarmerRequestRequest request) {

        // Step 1: Validate farmer
        User farmer = userRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + request.getFarmerId()));

        if (farmer.getRole() != Role.FARMER) {
            throw new BadRequestException("User is not a farmer");
        }
        if (farmer.getStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("Farmer account is not active");
        }

        // Step 2: Validate all products
        List<Product> products = new ArrayList<>();
        for (RequestItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new BadRequestException("Product is not available: " + product.getName());
            }
            products.add(product);
        }

        // Step 3: Find best warehouse
        Warehouse selectedWarehouse = findBestWarehouse(request.getItems(), products);

        // Step 4: Find least busy delivery agent
        User selectedAgent = findLeastBusyAgent();

        // Step 5: Build and save request header
        FarmerRequest farmerRequest = FarmerRequest.builder()
                .farmer(farmer)
                .warehouse(selectedWarehouse)
                .deliveryAgent(selectedAgent)
                .status(RequestStatus.SCHEDULED)
                .scheduledAt(LocalDateTime.now())
                .build();

        farmerRequest = farmerRequestRepository.save(farmerRequest);

        // Step 6: Build items and deduct inventory
        List<RequestItem> items = new ArrayList<>();
        for (int i = 0; i < request.getItems().size(); i++) {
            RequestItemRequest itemRequest = request.getItems().get(i);
            Product product = products.get(i);

            Inventory inventory = inventoryRepository
                    .findByWarehouseIdAndProductId(selectedWarehouse.getId(), product.getId())
                    .get();

            inventory.setQuantityAvailable(inventory.getQuantityAvailable() - itemRequest.getQuantity());
            inventory.setLastUpdatedAt(LocalDateTime.now());
            inventoryRepository.save(inventory);

            RequestItem item = RequestItem.builder()
                    .farmerRequest(farmerRequest)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .status(RequestItemStatus.ALLOCATED)
                    .build();

            items.add(requestItemRepository.save(item));
        }

        farmerRequest.setItems(items);

        // Auto-create delivery record
        deliveryService.createDelivery(farmerRequest);

        return toResponse(farmerRequest);
    }

    private Warehouse findBestWarehouse(List<RequestItemRequest> itemRequests, List<Product> products) {

        List<Warehouse> activeWarehouses = warehouseRepository.findByStatus(WarehouseStatus.ACTIVE);

        if (activeWarehouses.isEmpty()) {
            throw new InsufficientStockException("No active warehouses available");
        }

        for (Warehouse warehouse : activeWarehouses) {
            boolean canFulfillAll = true;

            for (int i = 0; i < itemRequests.size(); i++) {
                Product product = products.get(i);
                Double requestedQty = itemRequests.get(i).getQuantity();

                Inventory inventory = inventoryRepository
                        .findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                        .orElse(null);

                if (inventory == null || inventory.getQuantityAvailable() < requestedQty) {
                    canFulfillAll = false;
                    break;
                }
            }

            if (canFulfillAll) {
                return warehouse;
            }
        }

        // Build detailed error message
        StringBuilder errorMsg = new StringBuilder("No warehouse has sufficient stock for:\n");
        for (int i = 0; i < itemRequests.size(); i++) {
            Product product = products.get(i);
            Double requestedQty = itemRequests.get(i).getQuantity();

            Double bestAvailable = activeWarehouses.stream()
                    .map(wh -> inventoryRepository
                            .findByWarehouseIdAndProductId(wh.getId(), product.getId())
                            .map(Inventory::getQuantityAvailable)
                            .orElse(0.0))
                    .max(Double::compareTo)
                    .orElse(0.0);

            if (bestAvailable < requestedQty) {
                errorMsg.append("  - ")
                        .append(product.getName())
                        .append(": requested ").append(requestedQty).append(product.getUnit())
                        .append(", max available ").append(bestAvailable).append(product.getUnit())
                        .append("\n");
            }
        }

        throw new InsufficientStockException(errorMsg.toString());
    }

    private User findLeastBusyAgent() {
        List<User> agents = userRepository.findByRole(Role.DELIVERY)
                .stream()
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .collect(Collectors.toList());

        if (agents.isEmpty()) {
            throw new BadRequestException("No delivery agents are available at the moment");
        }

        User leastBusyAgent = null;
        long minDeliveries = Long.MAX_VALUE;

        for (User agent : agents) {
            long activeCount = farmerRequestRepository.countActiveDeliveriesByAgent(agent.getId());
            if (activeCount < minDeliveries) {
                minDeliveries = activeCount;
                leastBusyAgent = agent;
            }
        }

        return leastBusyAgent;
    }

    public List<FarmerRequestResponse> getAllRequests() {
        return farmerRequestRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public FarmerRequestResponse getRequestById(Long id) {
        FarmerRequest request = farmerRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
        return toResponse(request);
    }

    public List<FarmerRequestResponse> getRequestsByFarmer(Long farmerId) {
        return farmerRequestRepository.findByFarmerId(farmerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<FarmerRequestResponse> getRequestsByStatus(RequestStatus status) {
        return farmerRequestRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<FarmerRequestResponse> getRequestsByAgent(Long agentId) {
        return farmerRequestRepository.findByDeliveryAgentId(agentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public FarmerRequestResponse cancelRequest(Long requestId) {

        FarmerRequest request = farmerRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        if (request.getStatus() == RequestStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel a delivered request");
        }
        if (request.getStatus() == RequestStatus.CANCELLED) {
            throw new BadRequestException("Request is already cancelled");
        }

        // Restore inventory
        for (RequestItem item : request.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByWarehouseIdAndProductId(
                            request.getWarehouse().getId(),
                            item.getProduct().getId())
                    .orElse(null);

            if (inventory != null) {
                inventory.setQuantityAvailable(inventory.getQuantityAvailable() + item.getQuantity());
                inventory.setLastUpdatedAt(LocalDateTime.now());
                inventoryRepository.save(inventory);
            }
        }

        request.setStatus(RequestStatus.CANCELLED);
        return toResponse(farmerRequestRepository.save(request));
    }

    private FarmerRequestResponse toResponse(FarmerRequest request) {
        List<RequestItemResponse> itemResponses = new ArrayList<>();

        for (RequestItem item : request.getItems()) {
            BigDecimal price = item.getProduct().getPrice();
            BigDecimal total = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            itemResponses.add(RequestItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productUnit(item.getProduct().getUnit())
                    .productPrice(price)
                    .quantity(item.getQuantity())
                    .totalPrice(total)
                    .status(item.getStatus())
                    .build());
        }

        return FarmerRequestResponse.builder()
                .id(request.getId())
                .farmerId(request.getFarmer().getId())
                .farmerName(request.getFarmer().getName())
                .warehouseId(request.getWarehouse() != null ? request.getWarehouse().getId() : null)
                .warehouseName(request.getWarehouse() != null ? request.getWarehouse().getName() : null)
                .deliveryAgentId(request.getDeliveryAgent() != null ? request.getDeliveryAgent().getId() : null)
                .deliveryAgentName(request.getDeliveryAgent() != null ? request.getDeliveryAgent().getName() : null)
                .items(itemResponses)
                .status(request.getStatus())
                .requestedAt(request.getRequestedAt())
                .scheduledAt(request.getScheduledAt())
                .build();
    }
}