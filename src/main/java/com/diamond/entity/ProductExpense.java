package com.diamond.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One product has exactly one expense record
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "solitaire_diamond", nullable = false)
    private Double solitaireDiamond;

    @Column(name = "loose_diamond", nullable = false)
    private Double looseDiamond;

    @Column(name = "gold_price", nullable = false)
    private Double goldPrice;

    @Column(name = "other_expense", nullable = false)
    private Double otherExpense;

    // Auto-computed on save — sum of all four buckets
    @Column(name = "total_expense", nullable = false)
    private Double totalExpense;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        computeTotalExpense();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        computeTotalExpense();
    }

    // Always recompute before any save or update
    public void computeTotalExpense() {
        this.totalExpense = (this.solitaireDiamond != null ? this.solitaireDiamond : 0.0)
                + (this.looseDiamond     != null ? this.looseDiamond     : 0.0)
                + (this.goldPrice        != null ? this.goldPrice        : 0.0)
                + (this.otherExpense     != null ? this.otherExpense     : 0.0);
    }
}