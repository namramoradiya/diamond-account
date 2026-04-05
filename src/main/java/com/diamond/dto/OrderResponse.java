package com.diamond.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long customerId;
    private String clientName;
    private String clientContact;
    private String city;
    private Integer relationshipNumber;
    private String notes;
    private LocalDateTime createdAt;
}