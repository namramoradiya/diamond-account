package com.diamond.repository;

import com.diamond.entity.ProductExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductExpenseRepository extends JpaRepository<ProductExpense, Long> {

    // Get expense by product
    Optional<ProductExpense> findByProductId(Long productId);

    // Check if expense record already exists for a product
    boolean existsByProductId(Long productId);

    // All expenses for a customer (via product → order → customer)
    List<ProductExpense> findByProductOrderCustomerId(Long customerId);

    // Sum of all expenses across all products — dashboard
    @Query("SELECT COALESCE(SUM(e.totalExpense), 0) FROM ProductExpense e")
    Double sumAllExpenses();

    // Sum of expenses for a specific customer
    @Query("SELECT COALESCE(SUM(e.totalExpense), 0) FROM ProductExpense e " +
            "WHERE e.product.order.customer.id = :customerId")
    Double sumExpensesByCustomerId(Long customerId);
}