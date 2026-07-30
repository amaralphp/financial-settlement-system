package com.financialsettlement.application.usecase;

import com.financialsettlement.application.event.PaymentValidatedEvent;
import com.financialsettlement.application.port.input.ValidatePaymentUseCase;
import com.financialsettlement.application.port.output.EventPublisher;
import com.financialsettlement.application.port.output.PaymentRepository;
import com.financialsettlement.domain.entity.Payment;
import com.financialsettlement.domain.service.PaymentValidatorService;
import com.financialsettlement.domain.valueobject.PaymentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ValidatePaymentService implements ValidatePaymentUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(ValidatePaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentValidatorService validatorService;
    private final EventPublisher eventPublisher;

    public ValidatePaymentService(PaymentRepository paymentRepository,
                                  PaymentValidatorService validatorService,
                                  EventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.validatorService = validatorService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Payment validatePayment(UUID paymentId) {
        var id = PaymentId.fromUUID(paymentId);
        var payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (!payment.isPending()) {
            LOG.warn("Payment {} is not in pending state, current status: {}", paymentId, payment.getStatus());
            throw new IllegalStateException("Payment is not in pending state");
        }

        var errors = validatorService.validate(payment);
        if (!errors.isEmpty()) {
            payment.markAsFailed(String.join(", ", errors));
            paymentRepository.save(payment);
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }

        payment.markAsValidated();
        var saved = paymentRepository.save(payment);

        var event = new PaymentValidatedEvent(
                saved.getId().toString(),
                saved.getAccountId(),
                saved.getMoney().getAmount(),
                saved.getMoney().getCurrencyCode()
        );
        eventPublisher.publish("payment.validated", event);

        LOG.info("Payment {} validated successfully", paymentId);
        return saved;
    }
}
