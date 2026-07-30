package com.financialsettlement.domain.valueobject;

import java.util.UUID;

public record PaymentId(UUID value) {

    public PaymentId {
        if (value == null) {
            throw new IllegalArgumentException("PaymentId value must not be null");
        }
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId fromString(String id) {
        return new PaymentId(UUID.fromString(id));
    }

    public static PaymentId fromUUID(UUID uuid) {
        return new PaymentId(uuid);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
