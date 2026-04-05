package com.diamond.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {

    // ── Customer Stats ───────────────────────────────────────
    private Long totalCustomers;
    private Long excellentClients;   // relationship = 1
    private Long goodClients;        // relationship = 2
    private Long averageClients;     // relationship = 3

    // ── Order Stats ──────────────────────────────────────────
    private Long totalOrders;

    // ── Product Stats ────────────────────────────────────────
    private Long totalProducts;
    private Long designInProcess;
    private Long rawMaterialDone;
    private Long inProduction;
    private Long delivered;

    // ── Payment Stats ────────────────────────────────────────
    private Double totalRevenue;          // sum of totalAmount where fullPaymentDone = true
    private Double totalPendingBalance;   // sum of remaining balances
    private Double totalAdvanceCollected; // sum of all advance amounts where advanceDone = true
    private Long fullPaymentCount;
    private Long pendingPaymentCount;

    // ── Expense & Profit Stats ───────────────────────────────
    private Double grandTotalExpense;
    private Double netProfitOrLoss;
    private String netLabel;              // "PROFIT", "LOSS" or "BREAK_EVEN"

    // ── Monthly Completed Orders ─────────────────────────────
    // Products that are DELIVERED and fullPaymentDone = true
    // grouped by month
    private List<MonthlyCompletedDto> monthlyCompleted;

    // ── Recent Activity ──────────────────────────────────────
    // Last 5 products with their current status
    private List<RecentProductDto> recentProducts;

    // ── Top Clients ──────────────────────────────────────────
    // Clients with relationship = 1 and at least one order
    private List<TopClientDto> topClients;

    // ── Products Needing Attention ───────────────────────────
    // Pending payment + not yet delivered
    private List<PendingPaymentProductDto> pendingPaymentProducts;

    // ── Status Breakdown per Product Type ────────────────────
    private Map<String, Long> productTypeBreakdown;

    // ─────────────────────────────────────────────────────────
    // Nested DTOs
    // ─────────────────────────────────────────────────────────

    @Data
    @Builder
    public static class MonthlyCompletedDto {
        private String month;          // "April 2026"
        private Long completedCount;
        private Double revenueCollected;
    }

    @Data
    @Builder
    public static class RecentProductDto {
        private Long productId;
        private String productName;
        private String productType;
        private String status;
        private String clientName;
        private String orderNumber;
    }

    @Data
    @Builder
    public static class TopClientDto {
        private Long customerId;
        private String clientName;
        private String clientContact;
        private String city;
        private Long totalOrders;
        private Long totalProducts;
        private Double totalAmountBusiness;
    }

    @Data
    @Builder
    public static class PendingPaymentProductDto {
        private Long productId;
        private String productName;
        private String clientName;
        private String clientContact;
        private String status;
        private Double totalAmount;
        private Double advanceAmount;
        private Double remainingBalance;
        private Boolean advanceDone;
    }
}