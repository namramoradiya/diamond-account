package com.diamond.dto;

import com.diamond.entity.enums.MetalType;
import com.diamond.entity.enums.ProductStatus;
import com.diamond.entity.enums.ProductType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be 2–100 characters")
    private String productName;

    @NotNull(message = "Product type is required — STUD, PENDANT or RING")
    private ProductType productType;

    @NotNull(message = "Product date is required")
    private LocalDate productDate;

    // imageUrl is set separately via upload — optional here
    private String imageUrl;

    // Status is optional on create — defaults to DESIGN_IN_PROCESS
    private ProductStatus status;

    @NotNull(message = "Diamond use is required — true or false")
    private Boolean diamondUse;

    // Required only when diamondUse is true
    @DecimalMin(value = "0.01", message = "Diamond carat must be greater than 0")
    private Double diamondCarat;

    @NotNull(message = "Metal type is required — GOLD, SILVER or OTHER")
    private MetalType metalType;

    // Required only when metalType is OTHER
    private String metalDescription;

    @DecimalMin(value = "0.01", message = "Metal weight must be greater than 0")
    private Double metalWeight;

    @DecimalMin(value = "0.01", message = "Metal carat must be greater than 0")
    private Double metalCarat;

    private String notes;
}