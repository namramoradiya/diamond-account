package com.diamond.repository;

import com.diamond.entity.Product;
import com.diamond.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // All products under a specific order
    List<Product> findByOrderId(Long orderId);

    // All products for a specific customer (via order)
    List<Product> findByOrderCustomerId(Long customerId);

    // Products filtered by status (used in dashboard later)
    List<Product> findByStatus(ProductStatus status);

    // Products by order and status
    List<Product> findByOrderIdAndStatus(Long orderId, ProductStatus status);
}