package org.hamit.batchdemo.batch.base;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public abstract class BaseBatch<R, W> extends BaseListeners<R, W> implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(BaseBatch.class);
    @Autowired
    private Environment environment;
    private BatchConfigDTO batchConfig;

    @Autowired
    @Getter
    PlatformTransactionManager transactionManager;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(batchConfig.corePoolSize());
        taskExecutor.setMaxPoolSize(batchConfig.maxPoolSize());
        taskExecutor.setQueueCapacity(batchConfig.queueCapacity());

        return taskExecutor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Batch initialized : {}", this.getClass().getSimpleName());
        batchConfig = getBatchConfig();
    }

    public BatchConfigDTO getBatchConfig() {
        return new BatchConfigDTO(
                getBatchConfigProperty("core-pool-size", Integer.class),
                getBatchConfigProperty("max-pool-size", Integer.class),
                getBatchConfigProperty("queue-capacity", Integer.class));
    }

    public <T> T getBatchConfigProperty(String property, Class<T> type) {
        return environment.getProperty("batch." + getBatchConfigPrefix() + "." + property, type);
    }

    public abstract String getBatchConfigPrefix();

}
