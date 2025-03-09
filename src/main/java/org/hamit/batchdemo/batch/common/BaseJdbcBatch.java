package org.hamit.batchdemo.batch.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public class BaseJdbcBatch {
    @Getter
    @Setter
    private String name;

    public BaseJdbcBatch(String name) {
        this.name = name;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }
}
