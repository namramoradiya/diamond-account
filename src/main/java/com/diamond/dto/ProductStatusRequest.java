package com.diamond.dto;

import com.diamond.entity.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductStatusRequest {

    @NotNull(message = "Status is required — DESIGN_IN_PROCESS, RAW_MATERIAL_DONE, IN_PRODUCTION or DELIVERED")
    private ProductStatus status;
}