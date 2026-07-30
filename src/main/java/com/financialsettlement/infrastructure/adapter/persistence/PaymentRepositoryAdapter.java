package com.financialsettlement.infrastructure.adapter.persistence;

import com.financialsettlement.application.port.output.PaymentRepository;
import com.financialsettlement.domain.entity.Payment;
import com.financialsettlement.domain.entity.PaymentStatus;
import com.financialsettlement.domain.valueobject.Money;
import com.financialsettlement.domain.valueobject.PaymentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentRepositoryAdapter implements PaymentRepository {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentRepositoryAdapter.class);

    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        var entity = toEntity(payment);
        var saved = jpaRepository.save(entity);
        LOG.debug("Payment {} saved with version {}", saved.getId(), saved.getVersion());
        return toDomain(saved);
    }

    @Override
    public Optional<Payment> findById(PaymentId id) {
        return jpaRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Payment> findByAccountId(String accountId) {
        return jpaRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private PaymentEntity toEntity(Payment payment) {
        return new PaymentEntity(
                payment.getId().value(),
                payment.getAccountId(),
                payment.getMoney().getAmount(),
                payment.getMoney().getCurrencyCode(),
                payment.getDescription(),
                statusToString(payment.getStatus()),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getVersion()
        );
    }

    private Payment toDomain(PaymentEntity entity) {
        return new Payment(
                PaymentId.fromUUID(entity.getId()),
                entity.getAccountId(),
                new Money(entity.getAmount(), entity.getCurrency()),
                entity.getDescription(),
                stringToStatus(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    private String statusToString(PaymentStatus status) {
        return switch (status) {
            case PaymentStatus.Pending ignored -> "PENDING";
            case PaymentStatus.Validated ignored -> "VALIDATED";
            case PaymentStatus.Settled ignored -> "SETTLED";
            case PaymentStatus.Failed ignored -> "FAILED";
        };
    }

    private PaymentStatus stringToStatus(String status) {
        if (status == null) return PaymentStatus.Pending.INSTANCE;
        return switch (status.toUpperCase()) {
            case "PENDING" -> PaymentStatus.Pending.INSTANCE;
            case "VALIDATED" -> new PaymentStatus.Validated(java.util.UUID.randomUUID());
            case "SETTLED" -> new PaymentStatus.Settled(java.util.UUID.randomUUID());
            case "FAILED" -> new PaymentStatus.Failed("Unknown error");
            default -> {
                LOG.warn("Unknown payment status: {}, defaulting to PENDING", status);
                yield PaymentStatus.Pending.INSTANCE;
            }
        };
    }
}
