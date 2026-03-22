package com.agrosupply.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agrosupply.dto.request.ProductRequest;
import com.agrosupply.dto.response.ProductResponse;
import com.agrosupply.entity.Product;
import com.agrosupply.enums.ProductStatus;
import com.agrosupply.exception.ResourceNotFoundException;
import com.agrosupply.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // Create a new product
    public ProductResponse createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .unit(request.getUnit())
                .price(request.getPrice())
                .status(ProductStatus.ACTIVE)   // always starts as ACTIVE
                .build();

        return toResponse(productRepository.save(product));
    }

    // Get all products regardless of status — admin use
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get only ACTIVE products — farmer use (browse catalog)
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get products by category
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Get product by ID
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toResponse(product);
    }

    // Update product details
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setUnit(request.getUnit());
        product.setPrice(request.getPrice());

        return toResponse(productRepository.save(product));
    }

    // Activate or deactivate a product
    public ProductResponse updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStatus(status);
        return toResponse(productRepository.save(product));
    }

    // Convert entity → response DTO
    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .unit(product.getUnit())
                .price(product.getPrice())
                .status(product.getStatus())
                .build();
    }
}