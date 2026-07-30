package com.financialsettlement.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class VirtualThreadConfig {

    @Bean
    public TaskExecutor virtualThreadExecutor() {
        var executor = new SimpleAsyncTaskExecutor("virtual-");
        executor.setVirtualThreads(true);
        return executor;
    }

    @Bean
    public Executor asyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
