package com.diamond.repository;

import com.diamond.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Get payment by product
    Optional<Payment> findByProductId(Long productId);

    // Check if payment record already exists for a product
    boolean existsByProductId(Long productId);

    // All full payments done — used in dashboard
    List<Payment> findByFullPaymentDoneTrue();

    // All pending payments — full payment not yet done
    List<Payment> findByFullPaymentDoneFalse();

    // Payments by customer (via product → order → customer)
    List<Payment> findByProductOrderCustomerId(Long customerId);
}