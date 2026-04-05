package com.diamond.entity;

import com.diamond.entity.enums.MetalType;
import com.diamond.entity.enums.ProductStatus;
import com.diamond.entity.enums.ProductType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private ProductType productType;

    @Column(name = "product_date", nullable = false)
    private LocalDate productDate;

    // Stored as a file path or URL — image upload handled separately
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    // ── Diamond Details ──────────────────────────────────────
    @Column(name = "diamond_use", nullable = false)
    private Boolean diamondUse;

    // Nullable — only filled when diamondUse is true
    @Column(name = "diamond_carat")
    private Double diamondCarat;

    // ── Metal Details ────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "metal_type", nullable = false)
    private MetalType metalType;

    // Filled only when metalType is OTHER
    @Column(name = "metal_description", length = 255)
    private String metalDescription;

    @Column(name = "metal_weight")
    private Double metalWeight;

    @Column(name = "metal_carat")
    private Double metalCarat;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        // Default status when product is first created
        if (this.status == null) {
            this.status = ProductStatus.DESIGN_IN_PROCESS;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}