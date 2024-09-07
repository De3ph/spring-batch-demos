package org.hamit.batchdemo;

import org.hamit.batchdemo.batch.BaseBatch;
import org.hamit.batchdemo.batch.Constants.SampleJobProperties;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class SampleJob extends BaseBatch {
    public SampleJob() {
        super(SampleJobProperties.JOB_NAME);
    }

    @Bean(SampleJobProperties.JOB_NAME)
    public Job job(JobRepository jobRepository) {
        return new JobBuilder(SampleJobProperties.JOB_NAME, jobRepository)
                .start(step(jobRepository))
                .build();
    }

    @Bean(SampleJobProperties.STEP_NAME)
    public Step step(JobRepository jobRepository) {
        return new StepBuilder(SampleJobProperties.STEP_NAME, jobRepository)
                .tasklet(task(), transactionManager())
                // spring batch tracks step complete status, if step completed successfully, it prevents step to run again,
                // if 'allowStartIfComplete' not setting to true, then step will not start again
                .allowStartIfComplete(true)
                .build();
    }

    private Tasklet task() {
        return (contribution, chunkContext) -> {
            System.out.println("BATCH END");
            return RepeatStatus.FINISHED;
        };
    }
}
