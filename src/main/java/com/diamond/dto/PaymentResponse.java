package com.diamond.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private String clientName;

    // Core payment fields
    private Double totalAmount;
    private Double advanceAmount;
    private Boolean advanceDone;
    private Boolean fullPaymentDone;

    // Auto-computed fields
    private Double remainingBalance;

    // Payment timestamp when full payment is marked done
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}