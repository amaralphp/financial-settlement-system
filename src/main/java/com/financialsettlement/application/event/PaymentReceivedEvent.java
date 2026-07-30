package com.financialsettlement.application.event;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentReceivedEvent(
        String paymentId,
        String accountId,
        BigDecimal amount,
        String currency,
        String description,
        Instant timestamp
) {
    public PaymentReceivedEvent(String paymentId, String accountId, BigDecimal amount, String currency, String description) {
        this(paymentId, accountId, amount, currency, description, Instant.now());
    }
}
