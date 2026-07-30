package com.financialsettlement.domain.entity;

import com.financialsettlement.domain.valueobject.Money;
import com.financialsettlement.domain.valueobject.PaymentId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

public class Payment {

    private static final Logger LOG = LoggerFactory.getLogger(Payment.class);

    private PaymentId id;
    private String accountId;
    private Money money;
    private String description;
    private PaymentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    public Payment(PaymentId id, String accountId, Money money, String description) {
        this.id = id;
        this.accountId = accountId;
        this.money = money;
        this.description = description;
        this.status = PaymentStatus.Pending.INSTANCE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.version = 0L;
    }

    public Payment(PaymentId id, String accountId, Money money, String description,
                   PaymentStatus status, Instant createdAt, Instant updatedAt, Long version) {
        this.id = id;
        this.accountId = accountId;
        this.money = money;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    public Payment markAsValidated() {
        if (!(status instanceof PaymentStatus.Pending)) {
            throw new IllegalStateException("Only pending payments can be validated. Current status: " + status);
        }
        this.status = new PaymentStatus.Validated(UUID.randomUUID());
        this.updatedAt = Instant.now();
        LOG.info("Payment {} validated successfully", id);
        return this;
    }

    public Payment markAsSettled() {
        if (!(status instanceof PaymentStatus.Validated)) {
            throw new IllegalStateException("Only validated payments can be settled. Current status: " + status);
        }
        this.status = new PaymentStatus.Settled(UUID.randomUUID());
        this.updatedAt = Instant.now();
        LOG.info("Payment {} settled successfully", id);
        return this;
    }

    public Payment markAsFailed(String reason) {
        if (status instanceof PaymentStatus.Settled) {
            throw new IllegalStateException("Settled payments cannot be marked as failed");
        }
        this.status = new PaymentStatus.Failed(reason);
        this.updatedAt = Instant.now();
        LOG.warn("Payment {} failed: {}", id, reason);
        return this;
    }

    public void incrementVersion() {
        this.version = (version == null ? 0L : version) + 1L;
    }

    public PaymentId getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public Money getMoney() {
        return money;
    }

    public String getDescription() {
        return description;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public boolean isPending() {
        return status instanceof PaymentStatus.Pending;
    }

    public boolean isValidated() {
        return status instanceof PaymentStatus.Validated;
    }

    public boolean isSettled() {
        return status instanceof PaymentStatus.Settled;
    }

    public boolean isFailed() {
        return status instanceof PaymentStatus.Failed;
    }
}
