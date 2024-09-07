package org.hamit.batchdemo.batch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
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
        return new JpaTransactionManager();
    }

}
