package com.diamond.service;

import com.diamond.dto.CustomerRequest;
import com.diamond.dto.CustomerResponse;
import com.diamond.entity.Customer;
import com.diamond.exception.ResourceNotFoundException;
import com.diamond.exception.UsernameAlreadyExistsException;
import com.diamond.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // ── Create ──────────────────────────────────────────────
    public CustomerResponse addCustomer(CustomerRequest request) {
        if (customerRepository.existsByClientContact(request.getClientContact())) {
            throw new IllegalArgumentException(
                    "A customer with contact '" + request.getClientContact() + "' already exists"
            );
        }
        Customer customer = mapToEntity(request);
        return mapToResponse(customerRepository.save(customer));
    }

    // ── Read All ─────────────────────────────────────────────
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Read One ─────────────────────────────────────────────
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    // ── Update ───────────────────────────────────────────────
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id));

        // Check if the new contact belongs to a DIFFERENT customer
        if (customerRepository.existsByClientContactAndIdNot(
                request.getClientContact(), id)) {
            throw new IllegalArgumentException(
                    "Contact '" + request.getClientContact() + "' is already used by another customer"
            );
        }

        customer.setClientName(request.getClientName());
        customer.setClientContact(request.getClientContact());
        customer.setState(request.getState());
        customer.setCity(request.getCity());
        customer.setPincode(request.getPincode());
        customer.setRelationshipNumber(request.getRelationshipNumber());

        return mapToResponse(customerRepository.save(customer));
    }

    // ── Delete ───────────────────────────────────────────────
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    // ── Mappers ──────────────────────────────────────────────
    private Customer mapToEntity(CustomerRequest request) {
        return Customer.builder()
                .clientName(request.getClientName())
                .clientContact(request.getClientContact())
                .state(request.getState())
                .city(request.getCity())
                .pincode(request.getPincode())
                .relationshipNumber(request.getRelationshipNumber())
                .build();
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .clientName(customer.getClientName())
                .clientContact(customer.getClientContact())
                .state(customer.getState())
                .city(customer.getCity())
                .pincode(customer.getPincode())
                .relationshipNumber(customer.getRelationshipNumber())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}