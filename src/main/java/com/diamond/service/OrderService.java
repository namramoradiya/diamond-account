package com.diamond.service;

import com.diamond.dto.OrderRequest;
import com.diamond.dto.OrderResponse;
import com.diamond.entity.Customer;
import com.diamond.entity.Order;
import com.diamond.exception.ResourceNotFoundException;
import com.diamond.repository.CustomerRepository;
import com.diamond.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    // ── Create ──────────────────────────────────────────────
    public OrderResponse createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + request.getCustomerId()));

        String orderNumber = generateOrderNumber();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .notes(request.getNotes())
                .build();

        return mapToResponse(orderRepository.save(order));
    }

    // ── Get All Orders ───────────────────────────────────────
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Orders by Customer ───────────────────────────────
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found with id: " + customerId);
        }
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ── Get Order by ID ──────────────────────────────────────
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id));
        return mapToResponse(order);
    }

    // ── Update Notes ─────────────────────────────────────────
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id));

        // Allow updating notes only
        // Customer cannot be changed after order creation
        order.setNotes(request.getNotes());

        return mapToResponse(orderRepository.save(order));
    }

    // ── Delete ───────────────────────────────────────────────
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Order not found with id: " + id);
        }
        orderRepository.deleteById(id);
    }

    // ── Order Number Generator ───────────────────────────────
    // Generates ORD-0001, ORD-0002, ORD-0003 ...
    private String generateOrderNumber() {
        long count = orderRepository.count() + 1;
        return String.format("ORD-%04d", count);
    }

    // ── Mapper ───────────────────────────────────────────────
    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .clientName(order.getCustomer().getClientName())
                .clientContact(order.getCustomer().getClientContact())
                .city(order.getCustomer().getCity())
                .relationshipNumber(order.getCustomer().getRelationshipNumber())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .build();
    }
}