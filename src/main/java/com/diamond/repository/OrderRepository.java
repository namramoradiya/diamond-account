package com.diamond.repository;

import com.diamond.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders for a specific customer
    List<Order> findByCustomerId(Long customerId);

    // For auto-generating order numbers
    long countByOrderNumberStartingWith(String prefix);

    // Check if order belongs to a customer (for validation)
    Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId);
}