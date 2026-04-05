package com.diamond.controller;

import com.diamond.dto.ProductResponse;
import com.diamond.dto.ProductStatusRequest;
import com.diamond.entity.enums.ProductStatus;
import com.diamond.service.ProductStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductStatusController {

    private final ProductStatusService productStatusService;

    // PATCH — only updating one field (status), so PATCH is correct here not PUT
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductStatusRequest request) {
        return ResponseEntity.ok(productStatusService.updateStatus(id, request));
    }

    // Get all products filtered by a specific status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductResponse>> getProductsByStatus(
            @PathVariable ProductStatus status) {
        return ResponseEntity.ok(productStatusService.getProductsByStatus(status));
    }

    // Summary count of all statuses — dashboard will use this
    @GetMapping("/status/summary")
    public ResponseEntity<Map<String, Long>> getStatusSummary() {
        return ResponseEntity.ok(productStatusService.getStatusSummary());
    }
}