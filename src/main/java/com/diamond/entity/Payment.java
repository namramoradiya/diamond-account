package com.diamond.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One product has exactly one payment record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "advance_amount", nullable = false)
    private Double advanceAmount;

    @Column(name = "advance_done", nullable = false)
    private Boolean advanceDone;

    @Column(name = "full_payment_done", nullable = false)
    private Boolean fullPaymentDone;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        // Auto-set payment date when full payment is marked done
        if (Boolean.TRUE.equals(this.fullPaymentDone) && this.paymentDate == null) {
            this.paymentDate = LocalDateTime.now();
        }
    }
}