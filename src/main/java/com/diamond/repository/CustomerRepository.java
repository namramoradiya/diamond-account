package com.diamond.repository;

import com.diamond.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByClientContact(String clientContact);
    Optional<Customer> findByClientContact(String clientContact);
    // For checking duplicate contact on update — exclude self
    boolean existsByClientContactAndIdNot(String clientContact, Long id);
}