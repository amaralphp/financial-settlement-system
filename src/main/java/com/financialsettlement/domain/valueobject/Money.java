package com.financialsettlement.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, String currencyCode) {

    private static final int DECIMAL_SCALE = 2;

    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new IllegalArgumentException("Currency code must not be blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        amount = amount.setScale(DECIMAL_SCALE, RoundingMode.HALF_EVEN);
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + currencyCode, e);
        }
    }

    public Money add(Money other) {
        if (!currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot add different currencies: " + currencyCode + " and " + other.currencyCode);
        }
        return new Money(amount.add(other.amount), currencyCode);
    }

    public Money subtract(Money other) {
        if (!currencyCode.equals(other.currencyCode)) {
            throw new IllegalArgumentException("Cannot subtract different currencies: " + currencyCode + " and " + other.currencyCode);
        }
        return new Money(amount.subtract(other.amount), currencyCode);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
