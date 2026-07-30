package com.financialsettlement.infrastructure.config;

import com.financialsettlement.application.port.input.ProcessPaymentUseCase;
import com.financialsettlement.application.port.input.SettlePaymentUseCase;
import com.financialsettlement.application.port.input.ValidatePaymentUseCase;
import com.financialsettlement.application.port.output.EventPublisher;
import com.financialsettlement.application.port.output.IdempotencyService;
import com.financialsettlement.application.port.output.PaymentRepository;
import com.financialsettlement.application.usecase.ProcessPaymentService;
import com.financialsettlement.application.usecase.SettlePaymentService;
import com.financialsettlement.application.usecase.ValidatePaymentService;
import com.financialsettlement.domain.service.PaymentValidatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentValidatorService paymentValidatorService() {
        return new PaymentValidatorService();
    }

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(PaymentValidatorService validatorService,
                                                       PaymentRepository paymentRepository,
                                                       EventPublisher eventPublisher,
                                                       IdempotencyService idempotencyService) {
        return new ProcessPaymentService(validatorService, paymentRepository, eventPublisher, idempotencyService);
    }

    @Bean
    public ValidatePaymentUseCase validatePaymentUseCase(PaymentRepository paymentRepository,
                                                         PaymentValidatorService validatorService,
                                                         EventPublisher eventPublisher) {
        return new ValidatePaymentService(paymentRepository, validatorService, eventPublisher);
    }

    @Bean
    public SettlePaymentUseCase settlePaymentUseCase(PaymentRepository paymentRepository,
                                                     PaymentValidatorService validatorService,
                                                     EventPublisher eventPublisher) {
        return new SettlePaymentService(paymentRepository, validatorService, eventPublisher);
    }
}
