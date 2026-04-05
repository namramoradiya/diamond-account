package com.diamond.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Total amount must be greater than 0")
    private Double totalAmount;

    @NotNull(message = "Advance amount is required")
    @DecimalMin(value = "0.0",
            message = "Advance amount cannot be negative")
    private Double advanceAmount;

    @NotNull(message = "Advance done status is required")
    private Boolean advanceDone;

    @NotNull(message = "Full payment done status is required")
    private Boolean fullPaymentDone;
}