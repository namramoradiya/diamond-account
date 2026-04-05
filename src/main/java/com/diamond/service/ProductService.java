package com.diamond.service;

import com.diamond.dto.ProductRequest;
import com.diamond.dto.ProductResponse;
import com.diamond.entity.Order;
import com.diamond.entity.Product;
import com.diamond.entity.enums.ProductStatus;
import com.diamond.exception.ResourceNotFoundException;
import com.diamond.repository.OrderRepository;
import com.diamond.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    // ── Create ──────────────────────────────────────────────
    public ProductResponse addProduct(ProductRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + request.getOrderId()));

        validateProductRequest(request);

        Product product = Product.builder()
                .order(order)
                .productName(request.getProductName())
                .productType(request.getProductType())
                .productDate(request.getProductDate())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus() != null
                        ? request.getStatus()
                        : ProductStatus.DESIGN_IN_PROCESS)
                .diamondUse(request.getDiamondUse())
                .diamondCarat(request.getDiamondUse() ? request.getDiamondCarat() : null)
                .metalType(request.getMetalType())
                .metalDescription(request.getMetalDescription())
                .metalWeight(request.getMetalWeight())
                .metalCarat(request.getMetalCarat())
                .notes(request.getNotes())
                .build();

        return mapToResponse(productRepository.save(product));
    }

    // ── Get All Products ─────────────────────────────────────
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Products by Order ────────────────────────────────
    public List<ProductResponse> getProductsByOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(
                    "Order not found with id: " + orderId);
        }
        return productRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Products by Customer ─────────────────────────────
    public List<ProductResponse> getProductsByCustomer(Long customerId) {
        return productRepository.findByOrderCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Product by ID ────────────────────────────────────
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
        return mapToResponse(product);
    }

    // ── Update Product ───────────────────────────────────────
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        validateProductRequest(request);

        product.setProductName(request.getProductName());
        product.setProductType(request.getProductType());
        product.setProductDate(request.getProductDate());
        product.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        product.setDiamondUse(request.getDiamondUse());
        product.setDiamondCarat(request.getDiamondUse() ? request.getDiamondCarat() : null);
        product.setMetalType(request.getMetalType());
        product.setMetalDescription(request.getMetalDescription());
        product.setMetalWeight(request.getMetalWeight());
        product.setMetalCarat(request.getMetalCarat());
        product.setNotes(request.getNotes());

        return mapToResponse(productRepository.save(product));
    }

    // ── Delete ───────────────────────────────────────────────
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // ── Business Validation ──────────────────────────────────
    private void validateProductRequest(ProductRequest request) {
        // If diamond is used, carat is mandatory
        if (Boolean.TRUE.equals(request.getDiamondUse())
                && request.getDiamondCarat() == null) {
            throw new IllegalArgumentException(
                    "Diamond carat is required when diamond use is true");
        }
        // If metal is OTHER, description is mandatory
        if (request.getMetalType() != null
                && request.getMetalType().name().equals("OTHER")
                && (request.getMetalDescription() == null
                || request.getMetalDescription().isBlank())) {
            throw new IllegalArgumentException(
                    "Metal description is required when metal type is OTHER");
        }
    }

    // ── Mapper ───────────────────────────────────────────────
    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .orderId(product.getOrder().getId())
                .orderNumber(product.getOrder().getOrderNumber())
                .customerId(product.getOrder().getCustomer().getId())
                .clientName(product.getOrder().getCustomer().getClientName())
                .productName(product.getProductName())
                .productType(product.getProductType())
                .productDate(product.getProductDate())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .diamondUse(product.getDiamondUse())
                .diamondCarat(product.getDiamondCarat())
                .metalType(product.getMetalType())
                .metalDescription(product.getMetalDescription())
                .metalWeight(product.getMetalWeight())
                .metalCarat(product.getMetalCarat())
                .notes(product.getNotes())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}