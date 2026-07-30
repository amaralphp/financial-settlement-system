package com.financialsettlement.application.port.input;

import com.financialsettlement.domain.entity.Payment;

public interface SettlePaymentUseCase {
    Payment settlePayment(java.util.UUID paymentId);
}
