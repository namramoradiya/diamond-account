package com.diamond.service;

import com.diamond.dto.ProductResponse;
import com.diamond.dto.ProductStatusRequest;
import com.diamond.entity.Product;
import com.diamond.entity.enums.ProductStatus;
import com.diamond.exception.ResourceNotFoundException;
import com.diamond.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductStatusService {

    private final ProductRepository productRepository;

    // ── Update Status ────────────────────────────────────────
    public ProductResponse updateStatus(Long productId, ProductStatusRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        validateStatusTransition(product.getStatus(), request.getStatus());

        product.setStatus(request.getStatus());
        return mapToResponse(productRepository.save(product));
    }

    // ── Get Products by Status ───────────────────────────────
    public List<ProductResponse> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Status Summary (count per status) ────────────────
    // Useful for dashboard later
    public Map<String, Long> getStatusSummary() {
        long designInProcess = productRepository
                .findByStatus(ProductStatus.DESIGN_IN_PROCESS).size();
        long rawMaterialDone = productRepository
                .findByStatus(ProductStatus.RAW_MATERIAL_DONE).size();
        long inProduction = productRepository
                .findByStatus(ProductStatus.IN_PRODUCTION).size();
        long delivered = productRepository
                .findByStatus(ProductStatus.DELIVERED).size();

        return Map.of(
                "DESIGN_IN_PROCESS", designInProcess,
                "RAW_MATERIAL_DONE", rawMaterialDone,
                "IN_PRODUCTION",     inProduction,
                "DELIVERED",         delivered,
                "TOTAL",             designInProcess + rawMaterialDone
                        + inProduction + delivered
        );
    }

    // ── Status Transition Validation ─────────────────────────
    // Status must move forward — cannot go backwards
    // Order: DESIGN_IN_PROCESS → RAW_MATERIAL_DONE → IN_PRODUCTION → DELIVERED
    private void validateStatusTransition(ProductStatus current, ProductStatus requested) {
        // Allow setting same status (idempotent)
        if (current == requested) return;

        int currentOrder  = getStatusOrder(current);
        int requestedOrder = getStatusOrder(requested);

        if (requestedOrder < currentOrder) {
            throw new IllegalArgumentException(
                    "Invalid status transition. Cannot move backwards from '"
                            + current.name() + "' to '" + requested.name() + "'. "
                            + "Status must progress forward: "
                            + "DESIGN_IN_PROCESS → RAW_MATERIAL_DONE → IN_PRODUCTION → DELIVERED"
            );
        }
    }

    private int getStatusOrder(ProductStatus status) {
        return switch (status) {
            case DESIGN_IN_PROCESS -> 1;
            case RAW_MATERIAL_DONE -> 2;
            case IN_PRODUCTION     -> 3;
            case DELIVERED         -> 4;
        };
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