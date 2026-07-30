package com.financialsettlement.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank(message = "Account ID is required")
        @Size(max = 50, message = "Account ID must not exceed 50 characters")
        String accountId,

        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        String currency,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
