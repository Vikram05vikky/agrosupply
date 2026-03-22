package com.agrosupply.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agrosupply.dto.request.ProductRequest;
import com.agrosupply.dto.response.ProductResponse;
import com.agrosupply.enums.ProductStatus;
import com.agrosupply.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // POST /api/products — Admin creates a new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    // GET /api/products — Admin gets all products (active + inactive)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/products/active — Farmers browse only active products
    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        return ResponseEntity.ok(productService.getActiveProducts());
    }

    // GET /api/products/category?category=Seeds — Filter by category
    @GetMapping("/category")
    public ResponseEntity<List<ProductResponse>> getByCategory(@RequestParam String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    // GET /api/products/{id} — Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // PUT /api/products/{id} — Admin updates product details
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                          @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // PATCH /api/products/{id}/status?status=INACTIVE — Activate or deactivate
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateStatus(@PathVariable Long id,
                                                         @RequestParam ProductStatus status) {
        return ResponseEntity.ok(productService.updateProductStatus(id, status));
    }
}