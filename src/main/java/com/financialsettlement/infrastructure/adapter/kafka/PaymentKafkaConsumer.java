package com.financialsettlement.infrastructure.adapter.kafka;

import com.financialsettlement.application.event.PaymentReceivedEvent;
import com.financialsettlement.application.event.PaymentSettledEvent;
import com.financialsettlement.application.event.PaymentValidatedEvent;
import com.financialsettlement.application.port.input.ProcessPaymentUseCase;
import com.financialsettlement.application.port.input.SettlePaymentUseCase;
import com.financialsettlement.application.port.input.ValidatePaymentUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;



@Component
public class PaymentKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentKafkaConsumer.class);

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final ValidatePaymentUseCase validatePaymentUseCase;
    private final SettlePaymentUseCase settlePaymentUseCase;

    public PaymentKafkaConsumer(ProcessPaymentUseCase processPaymentUseCase,
                                ValidatePaymentUseCase validatePaymentUseCase,
                                SettlePaymentUseCase settlePaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.validatePaymentUseCase = validatePaymentUseCase;
        this.settlePaymentUseCase = settlePaymentUseCase;
    }

    @KafkaListener(topics = "payment.received", containerFactory = "paymentKafkaListenerContainerFactory")
    public void onPaymentReceived(@Payload PaymentReceivedEvent event, Acknowledgment ack) {
        try {
            LOG.info("Received payment event: {}", event.paymentId());
            processPaymentUseCase.processPayment(
                    event.accountId(),
                    event.amount(),
                    event.currency(),
                    event.description()
            );
            ack.acknowledge();
        } catch (Exception e) {
            LOG.error("Error processing payment received event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.validated", containerFactory = "paymentKafkaListenerContainerFactory")
    public void onPaymentValidated(@Payload PaymentValidatedEvent event, Acknowledgment ack) {
        try {
            LOG.info("Received payment validated event: {}", event.paymentId());
            validatePaymentUseCase.validatePayment(UUID.fromString(event.paymentId()));
            ack.acknowledge();
        } catch (Exception e) {
            LOG.error("Error processing payment validated event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = "payment.settled", containerFactory = "paymentKafkaListenerContainerFactory")
    public void onPaymentSettled(@Payload PaymentSettledEvent event, Acknowledgment ack) {
        try {
            LOG.info("Received payment settled event: {}", event.paymentId());
            settlePaymentUseCase.settlePayment(UUID.fromString(event.paymentId()));
            ack.acknowledge();
        } catch (Exception e) {
            LOG.error("Error processing payment settled event: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }
}
