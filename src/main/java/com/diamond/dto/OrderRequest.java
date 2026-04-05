package com.diamond.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    // notes is optional
    private String notes;
}