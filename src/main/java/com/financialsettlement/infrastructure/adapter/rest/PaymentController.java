package com.financialsettlement.infrastructure.adapter.rest;

import com.financialsettlement.application.port.input.ProcessPaymentUseCase;
import com.financialsettlement.application.port.output.PaymentRepository;
import com.financialsettlement.domain.entity.Payment;
import com.financialsettlement.domain.valueobject.PaymentId;
import com.financialsettlement.infrastructure.adapter.rest.dto.PaymentRequest;
import com.financialsettlement.infrastructure.adapter.rest.dto.PaymentResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final PaymentRepository paymentRepository;

    public PaymentController(ProcessPaymentUseCase processPaymentUseCase,
                             PaymentRepository paymentRepository) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        LOG.info("Creating payment for account: {} amount: {} {}", request.accountId(), request.amount(), request.currency());
        var payment = processPaymentUseCase.processPayment(
                request.accountId(),
                request.amount(),
                request.currency(),
                request.description()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        LOG.debug("Fetching payment: {}", id);
        var paymentId = PaymentId.fromUUID(id);
        return paymentRepository.findById(paymentId)
                .map(p -> ResponseEntity.ok(toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByAccount(@PathVariable String accountId) {
        LOG.debug("Fetching payments for account: {}", accountId);
        var payments = paymentRepository.findByAccountId(accountId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(payments);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId().toString(),
                payment.getAccountId(),
                payment.getMoney().getAmount(),
                payment.getMoney().getCurrencyCode(),
                statusName(payment.getStatus()),
                payment.getCreatedAt()
        );
    }

    private String statusName(Object status) {
        if (status == null) return "PENDING";
        var name = status.getClass().getSimpleName();
        return name.toUpperCase();
    }
}
