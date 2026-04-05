package com.diamond.service;

import com.diamond.dto.PaymentRequest;
import com.diamond.dto.PaymentResponse;
import com.diamond.entity.Payment;
import com.diamond.entity.Product;
import com.diamond.exception.ResourceNotFoundException;
import com.diamond.repository.PaymentRepository;
import com.diamond.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;

    // ── Create Payment ───────────────────────────────────────
    public PaymentResponse createPayment(PaymentRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.getProductId()));

        // One product can only have one payment record
        if (paymentRepository.existsByProductId(request.getProductId())) {
            throw new IllegalArgumentException(
                    "Payment record already exists for product id: "
                            + request.getProductId()
                            + ". Use PUT to update it.");
        }

        validatePaymentAmounts(request);

        Payment payment = Payment.builder()
                .product(product)
                .totalAmount(request.getTotalAmount())
                .advanceAmount(request.getAdvanceAmount())
                .advanceDone(request.getAdvanceDone())
                .fullPaymentDone(request.getFullPaymentDone())
                .build();

        return mapToResponse(paymentRepository.save(payment));
    }

    // ── Get Payment by Product ───────────────────────────────
    public PaymentResponse getPaymentByProduct(Long productId) {
        Payment payment = paymentRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No payment record found for product id: " + productId));
        return mapToResponse(payment);
    }

    // ── Get Payment by ID ────────────────────────────────────
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    // ── Get All Payments ─────────────────────────────────────
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Full Payments Done ───────────────────────────────
    public List<PaymentResponse> getFullPaymentsDone() {
        return paymentRepository.findByFullPaymentDoneTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Pending Payments ─────────────────────────────────
    public List<PaymentResponse> getPendingPayments() {
        return paymentRepository.findByFullPaymentDoneFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Payments by Customer ─────────────────────────────
    public List<PaymentResponse> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByProductOrderCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Update Payment ───────────────────────────────────────
    public PaymentResponse updatePayment(Long id, PaymentRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with id: " + id));

        validatePaymentAmounts(request);

        payment.setTotalAmount(request.getTotalAmount());
        payment.setAdvanceAmount(request.getAdvanceAmount());
        payment.setAdvanceDone(request.getAdvanceDone());
        payment.setFullPaymentDone(request.getFullPaymentDone());

        return mapToResponse(paymentRepository.save(payment));
    }

    // ── Payment Summary for Dashboard ────────────────────────
    public Map<String, Object> getPaymentSummary() {
        List<Payment> all = paymentRepository.findAll();

        double totalRevenue = all.stream()
                .filter(p -> Boolean.TRUE.equals(p.getFullPaymentDone()))
                .mapToDouble(Payment::getTotalAmount)
                .sum();

        double totalPending = all.stream()
                .filter(p -> Boolean.FALSE.equals(p.getFullPaymentDone()))
                .mapToDouble(p -> computeRemainingBalance(p))
                .sum();

        double totalAdvanceCollected = all.stream()
                .filter(p -> Boolean.TRUE.equals(p.getAdvanceDone()))
                .mapToDouble(Payment::getAdvanceAmount)
                .sum();

        return Map.of(
                "totalRevenue",           totalRevenue,
                "totalPendingBalance",    totalPending,
                "totalAdvanceCollected",  totalAdvanceCollected,
                "fullPaymentCount",       paymentRepository.findByFullPaymentDoneTrue().size(),
                "pendingPaymentCount",    paymentRepository.findByFullPaymentDoneFalse().size()
        );
    }

    // ── Business Validation ──────────────────────────────────
    private void validatePaymentAmounts(PaymentRequest request) {
        // Advance cannot exceed total
        if (request.getAdvanceAmount() > request.getTotalAmount()) {
            throw new IllegalArgumentException(
                    "Advance amount cannot be greater than total amount");
        }
        // If full payment is done, advance done must also be true
        if (Boolean.TRUE.equals(request.getFullPaymentDone())
                && Boolean.FALSE.equals(request.getAdvanceDone())) {
            throw new IllegalArgumentException(
                    "Advance done must be true if full payment is done");
        }
    }

    // ── Remaining Balance Calculator ─────────────────────────
    private Double computeRemainingBalance(Payment payment) {
        if (Boolean.TRUE.equals(payment.getFullPaymentDone())) {
            return 0.0;
        }
        return payment.getTotalAmount() - payment.getAdvanceAmount();
    }

    // ── Mapper ───────────────────────────────────────────────
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .productId(payment.getProduct().getId())
                .productName(payment.getProduct().getProductName())
                .orderId(payment.getProduct().getOrder().getId())
                .orderNumber(payment.getProduct().getOrder().getOrderNumber())
                .customerId(payment.getProduct().getOrder().getCustomer().getId())
                .clientName(payment.getProduct().getOrder().getCustomer().getClientName())
                .totalAmount(payment.getTotalAmount())
                .advanceAmount(payment.getAdvanceAmount())
                .advanceDone(payment.getAdvanceDone())
                .fullPaymentDone(payment.getFullPaymentDone())
                .remainingBalance(computeRemainingBalance(payment))
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}