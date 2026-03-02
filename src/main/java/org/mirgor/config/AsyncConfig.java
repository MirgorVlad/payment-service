package org.mirgor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${executor.db.core_pool_size:10}")
    private int CORE_POOL_SIZE;

    @Value("${executor.db.max_pool_size:20}")
    private int MAX_POOL_SIZE;

    @Value("${executor.db.queue_capacity:100}")
    private int QUEUE_CAPACITY;

    @Bean("dbTaskExecutor")
    public Executor dbTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("db-async-");
        executor.initialize();
        return executor;
    }
}
