package org.hamit.batchdemo.batch.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;

import lombok.NonNull;

public abstract class BaseListeners<R, W> {
    private static final Logger log = LoggerFactory.getLogger(BaseListeners.class);

    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("{} job execution starting...", jobExecution.getJobInstance().getJobName());
            }

            @Override
            public void afterJob(@NonNull JobExecution jobExecution) {
                ExitStatus exitStatus = jobExecution.getExitStatus();
                log.info("{} job finished with exit status : {}", jobExecution.getJobInstance().getJobName(), exitStatus);
            }

        };
    }

    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(@NonNull StepExecution stepExecution) {
                log.info("{} step execution starting...", stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
                ExitStatus exitStatus = stepExecution.getExitStatus();
                log.info("{} step execution finished with exit status: {}", stepExecution.getStepName(), exitStatus);
                return exitStatus;
            }
        };
    }

    public ItemReadListener<R> itemReadListener() {
        return new ItemReadListener<>() {
            @Override
            public void onReadError(@NonNull Exception ex) {
                log.error("Item read error: {}", ex.getMessage());
            }
        };
    }

    public ItemWriteListener<W> itemWriteListener() {
        return new ItemWriteListener<>() {
            @Override
            public void onWriteError(@NonNull Exception ex, @NonNull Chunk<? extends W> items) {
                log.error("{} item write error : {}", items.toString(), ex.getMessage());
            }
        };
    }

    public JobExecutionListener getJobExecutionListener() {
        return jobExecutionListener();
    }

    public StepExecutionListener getStepExecutionListener() {
        return stepExecutionListener();
    }

    public ItemReadListener<R> getItemReadListener() {
        return itemReadListener();
    }

    public ItemWriteListener<W> getItemWriteListener() {
        return itemWriteListener();
    }
}
