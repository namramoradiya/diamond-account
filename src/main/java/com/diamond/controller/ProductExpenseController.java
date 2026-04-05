package com.diamond.controller;

import com.diamond.dto.ProductExpenseRequest;
import com.diamond.dto.ProductExpenseResponse;
import com.diamond.service.ProductExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ProductExpenseController {

    private final ProductExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ProductExpenseResponse> createExpense(
            @Valid @RequestBody ProductExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.createExpense(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductExpenseResponse>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductExpenseResponse> getExpenseById(
            @PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    // Most common lookup — by product directly
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductExpenseResponse> getExpenseByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(expenseService.getExpenseByProduct(productId));
    }

    // All expenses across all products of a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProductExpenseResponse>> getExpensesByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(expenseService.getExpensesByCustomer(customerId));
    }

    // Overall expense + profit/loss summary — dashboard will use this
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getExpenseSummary() {
        return ResponseEntity.ok(expenseService.getExpenseSummary());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ProductExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request));
    }
}