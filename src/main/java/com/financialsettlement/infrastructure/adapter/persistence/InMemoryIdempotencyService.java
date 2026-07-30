package com.financialsettlement.infrastructure.adapter.persistence;

import com.financialsettlement.application.port.output.IdempotencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryIdempotencyService implements IdempotencyService {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryIdempotencyService.class);

    private final ConcurrentHashMap<String, Boolean> processedKeys = new ConcurrentHashMap<>();

    @Override
    public boolean isProcessed(String idempotencyKey) {
        var processed = processedKeys.containsKey(idempotencyKey);
        if (processed) {
            LOG.debug("Idempotency key already processed: {}", idempotencyKey);
        }
        return processed;
    }

    @Override
    public void markProcessed(String idempotencyKey) {
        processedKeys.put(idempotencyKey, true);
        LOG.debug("Idempotency key marked as processed: {}", idempotencyKey);
    }
}
