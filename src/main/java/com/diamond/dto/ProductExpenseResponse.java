package com.diamond.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductExpenseResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private String clientName;

    // Four expense buckets
    private Double solitaireDiamond;
    private Double looseDiamond;
    private Double goldPrice;
    private Double otherExpense;

    // Auto-computed
    private Double totalExpense;

    // Profit / Loss — computed by comparing with payment
    // Positive = Profit, Negative = Loss, null = no payment record yet
    private Double profitOrLoss;
    private String profitOrLossLabel;  // "PROFIT", "LOSS" or "BREAK_EVEN"

    // Payment context — shown alongside expense for full picture
    private Double totalAmount;        // from payment
    private Double remainingBalance;   // from payment

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}