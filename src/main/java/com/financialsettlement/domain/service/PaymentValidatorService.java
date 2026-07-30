package com.financialsettlement.domain.service;

import com.financialsettlement.domain.entity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class PaymentValidatorService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentValidatorService.class);

    public List<String> validate(Payment payment) {
        var errors = new ArrayList<String>();

        if (payment == null) {
            errors.add("Payment must not be null");
            return errors;
        }

        if (payment.getAccountId() == null || payment.getAccountId().isBlank()) {
            errors.add("Account ID must not be blank");
        }

        if (payment.getMoney() == null) {
            errors.add("Money must not be null");
        } else {
            try {
                if (payment.getMoney().getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    errors.add("Amount must be greater than zero");
                }
                if (payment.getMoney().getCurrencyCode() == null || payment.getMoney().getCurrencyCode().isBlank()) {
                    errors.add("Currency code must not be blank");
                } else {
                    Currency.getInstance(payment.getMoney().getCurrencyCode());
                }
            } catch (IllegalArgumentException e) {
                errors.add("Invalid currency: " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            LOG.warn("Payment validation failed for {}: {}", payment.getId(), errors);
        } else {
            LOG.debug("Payment {} validated successfully", payment.getId());
        }

        return errors;
    }

    public boolean isValid(Payment payment) {
        return validate(payment).isEmpty();
    }
}
