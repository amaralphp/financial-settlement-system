package com.financialsettlement.application.port.input;

import com.financialsettlement.domain.entity.Payment;

public interface ProcessPaymentUseCase {
    Payment processPayment(String accountId, java.math.BigDecimal amount, String currency, String description);
}
