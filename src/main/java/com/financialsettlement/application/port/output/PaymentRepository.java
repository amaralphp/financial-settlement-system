package com.financialsettlement.application.port.output;

import com.financialsettlement.domain.entity.Payment;
import com.financialsettlement.domain.valueobject.PaymentId;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(PaymentId id);
    List<Payment> findByAccountId(String accountId);
}
