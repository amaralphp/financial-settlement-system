package com.financialsettlement.application.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentValidatedEvent(
        String paymentId,
        String accountId,
        BigDecimal amount,
        String currency,
        Instant timestamp
) {
    public PaymentValidatedEvent(String paymentId, String accountId, BigDecimal amount, String currency) {
        this(paymentId, accountId, amount, currency, Instant.now());
    }
}
