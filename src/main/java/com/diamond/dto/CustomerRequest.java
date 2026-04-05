package com.diamond.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRequest {

    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String clientName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Enter a valid 10-digit Indian mobile number")
    private String clientContact;

    @Size(max = 100, message = "State must be under 100 characters")
    private String state;

    @Size(max = 100, message = "City must be under 100 characters")
    private String city;

    @Pattern(regexp = "^[1-9][0-9]{5}$",
            message = "Enter a valid 6-digit pincode")
    private String pincode;

    @NotNull(message = "Relationship number is required")
    @Min(value = 1, message = "Relationship number must be 1, 2, or 3")
    @Max(value = 3, message = "Relationship number must be 1, 2, or 3")
    private Integer relationshipNumber;
}