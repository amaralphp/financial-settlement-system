package com.financialsettlement.application.usecase;

import com.financialsettlement.application.event.PaymentReceivedEvent;
import com.financialsettlement.application.port.input.ProcessPaymentUseCase;
import com.financialsettlement.application.port.output.EventPublisher;
import com.financialsettlement.application.port.output.IdempotencyService;
import com.financialsettlement.application.port.output.PaymentRepository;
import com.financialsettlement.domain.entity.Payment;
import com.financialsettlement.domain.service.PaymentValidatorService;
import com.financialsettlement.domain.valueobject.Money;
import com.financialsettlement.domain.valueobject.PaymentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class ProcessPaymentService implements ProcessPaymentUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessPaymentService.class);

    private final PaymentValidatorService validatorService;
    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;
    private final IdempotencyService idempotencyService;

    public ProcessPaymentService(PaymentValidatorService validatorService,
                                 PaymentRepository paymentRepository,
                                 EventPublisher eventPublisher,
                                 IdempotencyService idempotencyService) {
        this.validatorService = validatorService;
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
        this.idempotencyService = idempotencyService;
    }

    @Override
    public Payment processPayment(String accountId, BigDecimal amount, String currency, String description) {
        var idempotencyKey = accountId + ":" + amount + ":" + currency;

        if (idempotencyService.isProcessed(idempotencyKey)) {
            LOG.warn("Duplicate payment request detected for key: {}", idempotencyKey);
            throw new IllegalStateException("Duplicate payment request");
        }

        var paymentId = PaymentId.generate();
        var money = new Money(amount, currency);
        var payment = new Payment(paymentId, accountId, money, description);

        var errors = validatorService.validate(payment);
        if (!errors.isEmpty()) {
            LOG.error("Payment validation failed: {}", errors);
            throw new IllegalArgumentException("Invalid payment: " + String.join(", ", errors));
        }

        var saved = paymentRepository.save(payment);
        idempotencyService.markProcessed(idempotencyKey);

        var event = new PaymentReceivedEvent(
                saved.getId().toString(),
                saved.getAccountId(),
                saved.getMoney().getAmount(),
                saved.getMoney().getCurrencyCode(),
                saved.getDescription()
        );
        eventPublisher.publish("payment.received", event);

        LOG.info("Payment {} processed successfully for account {}", saved.getId(), accountId);
        return saved;
    }
}
