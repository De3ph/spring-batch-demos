package org.hamit.batchdemo.batch.orderProduct;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hamit.batchdemo.batch.BatchConstants;
import org.hamit.batchdemo.batch.base.BaseAbstractBatch;
import org.hamit.batchdemo.batch.orderProduct.steps.OrderStep;
import org.hamit.batchdemo.dao.entity.Order;
import org.hamit.batchdemo.dao.entity.Product;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class OrderProductBatch extends BaseAbstractBatch<Product, Order> {
    private final OrderStep orderStep;

    @Bean(BatchConstants.JobNames.ORDER_PRODUCT_JOB)
    public Job job(JobRepository jobRepository) {
        return new JobBuilder(BatchConstants.JobNames.ORDER_PRODUCT_JOB, jobRepository)
                .start(orderStep(jobRepository))
                .listener(jobExecutionListener())
                .build();
    }

    // extracted bean is defined in OrderProductStepConfig
    @Bean(BatchConstants.StepNames.ORDER_PRODUCT_STEP)
    public Step orderStep(JobRepository jobRepository) {
        return new StepBuilder(BatchConstants.StepNames.ORDER_PRODUCT_STEP, jobRepository)
                .<Product, Order>chunk(getBatchProperties().chunkSize(), getTransactionManager())
                .listener(stepExecutionListener())
                .reader(orderStep.getReader(getBatchProperties().chunkSize()))
                .processor(orderStep.getProcessor())
                .writer(orderStep.getWriter())
                .listener(getItemReadListener())
                .listener(getItemWriteListener())
                .allowStartIfComplete(true)
                .build();
    }

    @Override
    public String getBatchConfigPrefix() {
        return BatchConstants.DEFAULT_BATCH_CONFIG_PREFIX;
    }
}
