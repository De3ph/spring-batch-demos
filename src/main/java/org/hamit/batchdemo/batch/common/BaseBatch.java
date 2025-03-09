package org.hamit.batchdemo.batch.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class BaseBatch  {
    @Getter
    @Setter
    private String name;

    public BaseBatch(String name) {
        this.name = name;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JdbcTransactionManager();
    }

}
