package com.financialsettlement.application.usecase;

import com.financialsettlement.application.event.PaymentSettledEvent;
import com.financialsettlement.application.port.input.SettlePaymentUseCase;
import com.financialsettlement.application.port.output.EventPublisher;
import com.financialsettlement.application.port.output.PaymentRepository;
import com.financialsettlement.domain.entity.Payment;
import com.financialsettlement.domain.service.PaymentValidatorService;
import com.financialsettlement.domain.valueobject.PaymentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SettlePaymentService implements SettlePaymentUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(SettlePaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentValidatorService validatorService;
    private final EventPublisher eventPublisher;

    public SettlePaymentService(PaymentRepository paymentRepository,
                                PaymentValidatorService validatorService,
                                EventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.validatorService = validatorService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Payment settlePayment(UUID paymentId) {
        var id = PaymentId.fromUUID(paymentId);
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (!payment.isValidated()) {
            LOG.warn("Payment {} is not in validated state, current status: {}", paymentId, payment.getStatus());
            throw new IllegalStateException("Payment must be validated before settlement");
        }

        payment.markAsSettled();
        var saved = paymentRepository.save(payment);

        var event = new PaymentSettledEvent(
                saved.getId().toString(),
                saved.getAccountId(),
                saved.getMoney().getAmount(),
                saved.getMoney().getCurrencyCode()
        );
        eventPublisher.publish("payment.settled", event);

        LOG.info("Payment {} settled successfully", paymentId);
        return saved;
    }
}
