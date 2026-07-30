package com.financialsettlement.infrastructure.adapter.rest.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        String id,
        String accountId,
        BigDecimal amount,
        String currency,
        String status,
        Instant createdAt
) {
}
