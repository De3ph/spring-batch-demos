package org.hamit.batchdemo.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hamit.batchdemo.batch.base.BaseBatch;
import org.hamit.batchdemo.dao.entity.Order;
import org.hamit.batchdemo.dao.entity.Product;
import org.hamit.batchdemo.dao.repository.OrderRepository;
import org.hamit.batchdemo.dao.repository.ProductRepository;
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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@Slf4j
@RequiredArgsConstructor
public class SampleBatch extends BaseBatch {

    private static final Integer PAGE_SIZE = 20;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;


    @Bean("sampleJob")
    public Job job(JobRepository jobRepository) {
        return new JobBuilder("sampleJob", jobRepository)
                .start(step1(jobRepository))
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Order>chunk(PAGE_SIZE, transactionManager())
                .reader(ItemReader())
                .processor(ItemProcessor())
                .writer(ItemWriter())
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
}
