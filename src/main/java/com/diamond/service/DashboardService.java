package com.diamond.service;

import com.diamond.dto.DashboardResponse;
import com.diamond.entity.*;
import com.diamond.entity.enums.ProductStatus;
import com.diamond.entity.enums.ProductType;
import com.diamond.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CustomerRepository   customerRepository;
    private final OrderRepository      orderRepository;
    private final ProductRepository    productRepository;
    private final PaymentRepository    paymentRepository;
    private final ProductExpenseRepository expenseRepository;

    public DashboardResponse getDashboard() {
        return DashboardResponse.builder()
                .totalCustomers(getTotalCustomers())
                .excellentClients(getClientsByRelationship(1))
                .goodClients(getClientsByRelationship(2))
                .averageClients(getClientsByRelationship(3))
                .totalOrders(getTotalOrders())
                .totalProducts(getTotalProducts())
                .designInProcess(getProductCountByStatus(ProductStatus.DESIGN_IN_PROCESS))
                .rawMaterialDone(getProductCountByStatus(ProductStatus.RAW_MATERIAL_DONE))
                .inProduction(getProductCountByStatus(ProductStatus.IN_PRODUCTION))
                .delivered(getProductCountByStatus(ProductStatus.DELIVERED))
                .totalRevenue(getTotalRevenue())
                .totalPendingBalance(getTotalPendingBalance())
                .totalAdvanceCollected(getTotalAdvanceCollected())
                .fullPaymentCount(getFullPaymentCount())
                .pendingPaymentCount(getPendingPaymentCount())
                .grandTotalExpense(getGrandTotalExpense())
                .netProfitOrLoss(getNetProfitOrLoss())
                .netLabel(getNetLabel())
                .monthlyCompleted(getMonthlyCompleted())
                .recentProducts(getRecentProducts())
                .topClients(getTopClients())
                .pendingPaymentProducts(getPendingPaymentProducts())
                .productTypeBreakdown(getProductTypeBreakdown())
                .build();
    }

    // ── Customer Stats ───────────────────────────────────────
    private Long getTotalCustomers() {
        return customerRepository.count();
    }

    private Long getClientsByRelationship(int rel) {
        return customerRepository.findAll()
                .stream()
                .filter(c -> c.getRelationshipNumber() == rel)
                .count();
    }

    // ── Order Stats ──────────────────────────────────────────
    private Long getTotalOrders() {
        return orderRepository.count();
    }

    // ── Product Stats ────────────────────────────────────────
    private Long getTotalProducts() {
        return productRepository.count();
    }

    private Long getProductCountByStatus(ProductStatus status) {
        return (long) productRepository.findByStatus(status).size();
    }

    // ── Payment Stats ────────────────────────────────────────
    private Double getTotalRevenue() {
        return paymentRepository.findByFullPaymentDoneTrue()
                .stream()
                .mapToDouble(Payment::getTotalAmount)
                .sum();
    }

    private Double getTotalPendingBalance() {
        return paymentRepository.findByFullPaymentDoneFalse()
                .stream()
                .mapToDouble(p -> p.getTotalAmount() - p.getAdvanceAmount())
                .sum();
    }

    private Double getTotalAdvanceCollected() {
        return paymentRepository.findAll()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getAdvanceDone()))
                .mapToDouble(Payment::getAdvanceAmount)
                .sum();
    }

    private Long getFullPaymentCount() {
        return (long) paymentRepository.findByFullPaymentDoneTrue().size();
    }

    private Long getPendingPaymentCount() {
        return (long) paymentRepository.findByFullPaymentDoneFalse().size();
    }

    // ── Expense & Profit Stats ───────────────────────────────
    private Double getGrandTotalExpense() {
        return expenseRepository.sumAllExpenses();
    }

    private Double getNetProfitOrLoss() {
        return getTotalRevenue() - getGrandTotalExpense();
    }

    private String getNetLabel() {
        double net = getNetProfitOrLoss();
        if (net > 0)  return "PROFIT";
        if (net < 0)  return "LOSS";
        return "BREAK_EVEN";
    }

    // ── Monthly Completed ────────────────────────────────────
    // A "completed" entry = product is DELIVERED + full payment done
    private List<DashboardResponse.MonthlyCompletedDto> getMonthlyCompleted() {
        List<Payment> fullPayments = paymentRepository.findByFullPaymentDoneTrue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        // Group by month of paymentDate
        Map<String, List<Payment>> grouped = fullPayments.stream()
                .filter(p -> p.getPaymentDate() != null)
                .filter(p -> p.getProduct().getStatus() == ProductStatus.DELIVERED)
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentDate().format(formatter)
                ));

        return grouped.entrySet().stream()
                .map(entry -> DashboardResponse.MonthlyCompletedDto.builder()
                        .month(entry.getKey())
                        .completedCount((long) entry.getValue().size())
                        .revenueCollected(entry.getValue().stream()
                                .mapToDouble(Payment::getTotalAmount)
                                .sum())
                        .build())
                .sorted(Comparator.comparing(DashboardResponse.MonthlyCompletedDto::getMonth)
                        .reversed())
                .toList();
    }

    // ── Recent Products ──────────────────────────────────────
    // Last 5 products created — quick activity feed
    private List<DashboardResponse.RecentProductDto> getRecentProducts() {
        return productRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
                .limit(5)
                .map(p -> DashboardResponse.RecentProductDto.builder()
                        .productId(p.getId())
                        .productName(p.getProductName())
                        .productType(p.getProductType().name())
                        .status(p.getStatus().name())
                        .clientName(p.getOrder().getCustomer().getClientName())
                        .orderNumber(p.getOrder().getOrderNumber())
                        .build())
                .toList();
    }

    // ── Top Clients ──────────────────────────────────────────
    // Relationship = 1, sorted by total business amount descending
    private List<DashboardResponse.TopClientDto> getTopClients() {
        return customerRepository.findAll()
                .stream()
                .filter(c -> c.getRelationshipNumber() == 1)
                .map(c -> {
                    List<Order> orders = orderRepository.findByCustomerId(c.getId());

                    long totalProducts = orders.stream()
                            .flatMap(o -> productRepository.findByOrderId(o.getId()).stream())
                            .count();

                    double totalBusiness = orders.stream()
                            .flatMap(o -> productRepository.findByOrderId(o.getId()).stream())
                            .map(p -> paymentRepository.findByProductId(p.getId()))
                            .filter(Optional::isPresent)
                            .mapToDouble(opt -> opt.get().getTotalAmount())
                            .sum();

                    return DashboardResponse.TopClientDto.builder()
                            .customerId(c.getId())
                            .clientName(c.getClientName())
                            .clientContact(c.getClientContact())
                            .city(c.getCity())
                            .totalOrders((long) orders.size())
                            .totalProducts(totalProducts)
                            .totalAmountBusiness(totalBusiness)
                            .build();
                })
                .sorted(Comparator.comparingDouble(
                        DashboardResponse.TopClientDto::getTotalAmountBusiness).reversed())
                .toList();
    }

    // ── Pending Payment Products ─────────────────────────────
    // Products where full payment is not done yet
    private List<DashboardResponse.PendingPaymentProductDto> getPendingPaymentProducts() {
        return paymentRepository.findByFullPaymentDoneFalse()
                .stream()
                .map(p -> DashboardResponse.PendingPaymentProductDto.builder()
                        .productId(p.getProduct().getId())
                        .productName(p.getProduct().getProductName())
                        .clientName(p.getProduct().getOrder()
                                .getCustomer().getClientName())
                        .clientContact(p.getProduct().getOrder()
                                .getCustomer().getClientContact())
                        .status(p.getProduct().getStatus().name())
                        .totalAmount(p.getTotalAmount())
                        .advanceAmount(p.getAdvanceAmount())
                        .remainingBalance(p.getTotalAmount() - p.getAdvanceAmount())
                        .advanceDone(p.getAdvanceDone())
                        .build())
                .sorted(Comparator.comparingDouble(
                                DashboardResponse.PendingPaymentProductDto::getRemainingBalance)
                        .reversed())
                .toList();
    }

    // ── Product Type Breakdown ───────────────────────────────
    private Map<String, Long> getProductTypeBreakdown() {
        Map<String, Long> breakdown = new LinkedHashMap<>();
        for (ProductType type : ProductType.values()) {
            long count = productRepository.findAll()
                    .stream()
                    .filter(p -> p.getProductType() == type)
                    .count();
            breakdown.put(type.name(), count);
        }
        return breakdown;
    }
}