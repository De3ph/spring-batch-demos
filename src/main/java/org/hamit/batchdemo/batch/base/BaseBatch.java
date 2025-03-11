package org.hamit.batchdemo.batch.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public abstract class BaseBatch implements InitializingBean {
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Batch initialized : {}", this.getClass().getSimpleName());
    }

}
