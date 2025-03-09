package org.hamit.batchdemo.batch;

import org.hamit.batchdemo.dao.person.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class JdbcJob {
    public static final String NAME = "JDBC_BATCH";

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    public JdbcCursorItemReader<Person> itemReader(DataSource dataSource) {
        return new JdbcCursorItemReaderBuilder<Person>()
                .name("personItemReader")
                .dataSource(dataSource)
                .sql("SELECT * FROM person")
                .rowMapper(new BeanPropertyRowMapper<>(Person.class))
                .build();
    }

    @Bean
    public ItemProcessor<Person,Person> itemProcessor() {
        return person -> person;
    }

    @Bean
    public JdbcBatchItemWriter<Person> itemWriter() {
        return new JdbcBatchItemWriterBuilder<Person>()
                .sql("INSERT INTO batch_person (id, fullName, birthDay) VALUES (:id, :fullName, :birthDay)")
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, DataSource dataSource, JdbcTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Person, Person>chunk(10, transactionManager)
                .reader(itemReader(dataSource))
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, DataSource dataSource, JdbcTransactionManager jdbcTransactionManager) {
        return new JobBuilder(NAME, jobRepository).start(step(jobRepository,dataSource,jdbcTransactionManager)).build();
    }


}
