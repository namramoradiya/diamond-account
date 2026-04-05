package com.diamond.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerResponse {
    private Long id;
    private String clientName;
    private String clientContact;
    private String state;
    private String city;
    private String pincode;
    private Integer relationshipNumber;
    private LocalDateTime createdAt;
}