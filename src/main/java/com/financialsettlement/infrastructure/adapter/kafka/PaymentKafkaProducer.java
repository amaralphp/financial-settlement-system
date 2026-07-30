package com.financialsettlement.infrastructure.adapter.kafka;

import com.financialsettlement.application.port.output.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentKafkaProducer implements EventPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentKafkaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public <T> void publish(String topic, T event) {
        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        LOG.debug("Event {} published to topic {} at offset {}",
                                event.getClass().getSimpleName(), topic, result.getRecordMetadata().offset());
                    } else {
                        LOG.error("Failed to publish event {} to topic {}: {}",
                                event.getClass().getSimpleName(), topic, ex.getMessage(), ex);
                    }
                });
        LOG.info("Published event {} to topic {}", event.getClass().getSimpleName(), topic);
    }
}
