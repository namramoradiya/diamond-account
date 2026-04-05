package com.diamond.dto;

import com.diamond.entity.enums.MetalType;
import com.diamond.entity.enums.ProductStatus;
import com.diamond.entity.enums.ProductType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private String clientName;
    private String productName;
    private ProductType productType;
    private LocalDate productDate;
    private String imageUrl;
    private ProductStatus status;
    private Boolean diamondUse;
    private Double diamondCarat;
    private MetalType metalType;
    private String metalDescription;
    private Double metalWeight;
    private Double metalCarat;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}