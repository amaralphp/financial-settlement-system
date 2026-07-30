package com.financialsettlement.application.port.output;

public interface EventPublisher {
    <T> void publish(String topic, T event);
}
