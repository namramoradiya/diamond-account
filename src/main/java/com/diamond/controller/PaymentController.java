package com.diamond.controller;

import com.diamond.dto.PaymentRequest;
import com.diamond.dto.PaymentResponse;
import com.diamond.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // Get payment directly by product id — most common lookup
    @GetMapping("/product/{productId}")
    public ResponseEntity<PaymentResponse> getPaymentByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(paymentService.getPaymentByProduct(productId));
    }

    // All payments by customer across all their orders and products
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    // Completed payments
    @GetMapping("/completed")
    public ResponseEntity<List<PaymentResponse>> getFullPaymentsDone() {
        return ResponseEntity.ok(paymentService.getFullPaymentsDone());
    }

    // Pending payments
    @GetMapping("/pending")
    public ResponseEntity<List<PaymentResponse>> getPendingPayments() {
        return ResponseEntity.ok(paymentService.getPendingPayments());
    }

    // Summary for dashboard
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getPaymentSummary() {
        return ResponseEntity.ok(paymentService.getPaymentSummary());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.updatePayment(id, request));
    }
}