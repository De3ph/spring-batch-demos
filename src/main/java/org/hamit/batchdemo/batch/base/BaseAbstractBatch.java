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
public abstract class BaseAbstractBatch<R, W> extends BaseAbstractListeners<R, W> implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(BaseAbstractBatch.class);
    @Autowired
    private Environment environment;
    @Getter
    private BatchConfigDTO batchProperties;

    @Autowired
    @Getter
    PlatformTransactionManager transactionManager;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(batchProperties.corePoolSize());
        taskExecutor.setMaxPoolSize(batchProperties.maxPoolSize());
        taskExecutor.setQueueCapacity(batchProperties.queueCapacity());

        return taskExecutor;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("{} initialized.", this.getClass().getSimpleName());
        batchProperties = getPropertiesFromEnv();
    }

    private BatchConfigDTO getPropertiesFromEnv() {
        return new BatchConfigDTO(
                getBatchConfigProperty("core-pool-size", Integer.class),
                getBatchConfigProperty("max-pool-size", Integer.class),
                getBatchConfigProperty("queue-capacity", Integer.class),
                getBatchConfigProperty("chunk-size", Integer.class)
                );
    }

    public <T> T getBatchConfigProperty(String property, Class<T> type) {
        return environment.getProperty("batch." + getBatchConfigPrefix() + "." + property, type);
    }

    /**
     * batch property'lerini yml dan okumak için
     * batch:
     *  {{batchConfigPrefix}}:
     *      core-pool-size: 5
     *      ...
     */
    public abstract String getBatchConfigPrefix();

}
