package org.hamit.batchdemo.batch;

import java.util.List;
import java.util.Map;

import org.hamit.batchdemo.batch.base.BaseBatch;
import org.hamit.batchdemo.dao.entity.Order;
import org.hamit.batchdemo.dao.entity.Product;
import org.hamit.batchdemo.dao.repository.OrderRepository;
import org.hamit.batchdemo.dao.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

@Configuration
@EnableBatchProcessing
public class SampleBatch extends BaseBatch<Product, Order> {
    private static final Logger log = LoggerFactory.getLogger(SampleBatch.class);
    private static final Integer PAGE_SIZE = 20;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public SampleBatch(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Bean(BatchConstants.SAMPLE_JOB_NAME)
    public Job job(JobRepository jobRepository) {
        return new JobBuilder(BatchConstants.SAMPLE_JOB_NAME, jobRepository)
                .start(step1(jobRepository))
                .listener(jobExecutionListener())
                .build();
    }

    @Bean(BatchConstants.SAMPLE_JOB_STEP)
    public Step step1(JobRepository jobRepository) {
        return new StepBuilder(BatchConstants.SAMPLE_JOB_STEP, jobRepository)
                .<Product, Order>chunk(PAGE_SIZE, transactionManager())
                .listener(stepExecutionListener())
                .reader(ItemReader())
                .listener(getItemReadListener())
                .processor(ItemProcessor())
                .writer(ItemWriter())
                .listener(getItemWriteListener())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public RepositoryItemReader<Product> ItemReader() {
        RepositoryItemReader<Product> reader = new RepositoryItemReader<>();
        reader.setRepository(productRepository);
        reader.setSort(Map.of("quantity", Sort.Direction.ASC));
        reader.setMethodName("findAllByQuantityLessThanEqual");
        reader.setPageSize(PAGE_SIZE);
        reader.setArguments(List.of(100));
        return reader;
    }

    @Bean
    public ItemProcessor<Product, Order> ItemProcessor() {
        return item -> {
            log.info("ITEM QUANTITY BELOW THAN 100, CURRENT QUANTITY: {}", item.getQuantity());
            Order order = new Order();
            order.addProduct(item);
            item.setOrderId(order);
            return order;
        };
    }

    @Bean
    public RepositoryItemWriter<Order> ItemWriter() {
        RepositoryItemWriter<Order> writer = new RepositoryItemWriter<>();
        writer.setRepository(orderRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Override
    public String getBatchConfigPrefix() {
        return BatchConstants.DEFAULT_BATCH_CONFIG_PREFIX;
    }
}
