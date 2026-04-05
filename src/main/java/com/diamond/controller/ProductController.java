package com.diamond.controller;

import com.diamond.dto.ProductRequest;
import com.diamond.dto.ProductResponse;
import com.diamond.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // All products under a specific order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ProductResponse>> getProductsByOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(productService.getProductsByOrder(orderId));
    }

    // All products across all orders for a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(productService.getProductsByCustomer(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}