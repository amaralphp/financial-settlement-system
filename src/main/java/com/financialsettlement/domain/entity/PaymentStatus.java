package com.financialsettlement.domain.entity;

import java.util.UUID;

public sealed interface PaymentStatus {

    record Pending() implements PaymentStatus {
        public static final PaymentStatus INSTANCE = new Pending();
    }

    record Validated(UUID validatedBy) implements PaymentStatus {
    }

    record Settled(UUID settledBy) implements PaymentStatus {
    }

    record Failed(String reason) implements PaymentStatus {
    }
}
