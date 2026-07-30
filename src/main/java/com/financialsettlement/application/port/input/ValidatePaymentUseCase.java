package com.financialsettlement.application.port.input;

import com.financialsettlement.domain.entity.Payment;

public interface ValidatePaymentUseCase {
    Payment validatePayment(java.util.UUID paymentId);
}
