package com.diamond.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductExpenseRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    // All four expense fields default to 0.0 if not provided
    // so owner can fill only what is applicable
    @NotNull(message = "Solitaire diamond expense is required — use 0 if not applicable")
    @DecimalMin(value = "0.0", message = "Solitaire diamond expense cannot be negative")
    private Double solitaireDiamond;

    @NotNull(message = "Loose diamond expense is required — use 0 if not applicable")
    @DecimalMin(value = "0.0", message = "Loose diamond expense cannot be negative")
    private Double looseDiamond;

    @NotNull(message = "Gold price is required — use 0 if not applicable")
    @DecimalMin(value = "0.0", message = "Gold price cannot be negative")
    private Double goldPrice;

    @NotNull(message = "Other expense is required — use 0 if not applicable")
    @DecimalMin(value = "0.0", message = "Other expense cannot be negative")
    private Double otherExpense;
}