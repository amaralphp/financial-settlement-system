package com.financialsettlement.application.port.output;

public interface IdempotencyService {
    boolean isProcessed(String idempotencyKey);
    void markProcessed(String idempotencyKey);
}
