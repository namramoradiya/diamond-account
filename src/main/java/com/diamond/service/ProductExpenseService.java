package com.diamond.service;

import com.diamond.dto.ProductExpenseRequest;
import com.diamond.dto.ProductExpenseResponse;
import com.diamond.entity.Payment;
import com.diamond.entity.Product;
import com.diamond.entity.ProductExpense;
import com.diamond.exception.ResourceNotFoundException;
import com.diamond.repository.PaymentRepository;
import com.diamond.repository.ProductExpenseRepository;
import com.diamond.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductExpenseService {

    private final ProductExpenseRepository expenseRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;

    // ── Create Expense ───────────────────────────────────────
    public ProductExpenseResponse createExpense(ProductExpenseRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        // One product can only have one expense record
        if (expenseRepository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException(
                    "Expense record already exists for product id: "
                            + request.getProductId()
                            + ". Use PUT to update it.");
        }

        ProductExpense expense = ProductExpense.builder()
                .product(product)
                .solitaireDiamond(request.getSolitaireDiamond())
                .looseDiamond(request.getLooseDiamond())
                .goldPrice(request.getGoldPrice())
                .otherExpense(request.getOtherExpense())
                .build();

        // totalExpense computed inside @PrePersist
        return mapToResponse(expenseRepository.save(expense));
    }

    // ── Get Expense by Product ───────────────────────────────
    public ProductExpenseResponse getExpenseByProduct(Long productId) {
        ProductExpense expense = expenseRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No expense record found for product id: " + productId));
        return mapToResponse(expense);
    }

    // ── Get Expense by ID ────────────────────────────────────
    public ProductExpenseResponse getExpenseById(Long id) {
        ProductExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense record not found with id: " + id));
        return mapToResponse(expense);
    }

    // ── Get All Expenses ─────────────────────────────────────
    public List<ProductExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Expenses by Customer ─────────────────────────────
    public List<ProductExpenseResponse> getExpensesByCustomer(Long customerId) {
        return expenseRepository.findByProductOrderCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Update Expense ───────────────────────────────────────
    public ProductExpenseResponse updateExpense(Long id, ProductExpenseRequest request) {
        ProductExpense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Expense record not found with id: " + id));

        expense.setSolitaireDiamond(request.getSolitaireDiamond());
        expense.setLooseDiamond(request.getLooseDiamond());
        expense.setGoldPrice(request.getGoldPrice());
        expense.setOtherExpense(request.getOtherExpense());

        // totalExpense recomputed inside @PreUpdate
        return mapToResponse(expenseRepository.save(expense));
    }

    // ── Overall Expense Summary for Dashboard ────────────────
    public Map<String, Object> getExpenseSummary() {
        List<ProductExpense> all = expenseRepository.findAll();

        double totalSolitaire = all.stream()
                .mapToDouble(ProductExpense::getSolitaireDiamond).sum();
        double totalLoose = all.stream()
                .mapToDouble(ProductExpense::getLooseDiamond).sum();
        double totalGold = all.stream()
                .mapToDouble(ProductExpense::getGoldPrice).sum();
        double totalOther = all.stream()
                .mapToDouble(ProductExpense::getOtherExpense).sum();
        double grandTotalExpense = expenseRepository.sumAllExpenses();

        // Total revenue from fully paid products
        double totalRevenue = paymentRepository.findByFullPaymentDoneTrue()
                .stream()
                .mapToDouble(Payment::getTotalAmount)
                .sum();

        double netProfitOrLoss = totalRevenue - grandTotalExpense;

        return Map.of(
                "totalSolitaireExpense",  totalSolitaire,
                "totalLooseExpense",      totalLoose,
                "totalGoldExpense",       totalGold,
                "totalOtherExpense",      totalOther,
                "grandTotalExpense",      grandTotalExpense,
                "totalRevenue",           totalRevenue,
                "netProfitOrLoss",        netProfitOrLoss,
                "netLabel",               netProfitOrLoss >= 0 ? "PROFIT" : "LOSS"
        );
    }

    // ── Profit / Loss Calculator ─────────────────────────────
    // profit = totalAmount (what customer pays) - totalExpense (what it cost)
    private Double computeProfitOrLoss(ProductExpense expense) {
        Optional<Payment> payment = paymentRepository
                .findByProductId(expense.getProduct().getId());

        // Cannot compute without a payment record
        if (payment.isEmpty()) return null;

        return payment.get().getTotalAmount() - expense.getTotalExpense();
    }

    private String computeProfitOrLossLabel(Double profitOrLoss) {
        if (profitOrLoss == null)  return "NO_PAYMENT_RECORD";
        if (profitOrLoss > 0)      return "PROFIT";
        if (profitOrLoss < 0)      return "LOSS";
        return "BREAK_EVEN";
    }

    // ── Mapper ───────────────────────────────────────────────
    private ProductExpenseResponse mapToResponse(ProductExpense expense) {
        Double profitOrLoss = computeProfitOrLoss(expense);

        // Pull payment context if available
        Optional<Payment> payment = paymentRepository
                .findByProductId(expense.getProduct().getId());

        Double totalAmount = payment.map(Payment::getTotalAmount).orElse(null);
        Double remainingBalance = payment.map(p ->
                Boolean.TRUE.equals(p.getFullPaymentDone())
                        ? 0.0
                        : p.getTotalAmount() - p.getAdvanceAmount()
        ).orElse(null);

        return ProductExpenseResponse.builder()
                .id(expense.getId())
                .productId(expense.getProduct().getId())
                .productName(expense.getProduct().getProductName())
                .orderId(expense.getProduct().getOrder().getId())
                .orderNumber(expense.getProduct().getOrder().getOrderNumber())
                .customerId(expense.getProduct().getOrder().getCustomer().getId())
                .clientName(expense.getProduct().getOrder().getCustomer().getClientName())
                .solitaireDiamond(expense.getSolitaireDiamond())
                .looseDiamond(expense.getLooseDiamond())
                .goldPrice(expense.getGoldPrice())
                .otherExpense(expense.getOtherExpense())
                .totalExpense(expense.getTotalExpense())
                .profitOrLoss(profitOrLoss)
                .profitOrLossLabel(computeProfitOrLossLabel(profitOrLoss))
                .totalAmount(totalAmount)
                .remainingBalance(remainingBalance)
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}