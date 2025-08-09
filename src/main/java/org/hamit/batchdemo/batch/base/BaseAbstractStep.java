package org.hamit.batchdemo.batch.base;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;

public abstract class BaseAbstractStep<I,O> {
    public abstract ItemReader<I> getReader(Integer PAGE_SIZE);

    public abstract ItemProcessor<I, O> getProcessor();

    public abstract ItemWriter<O> getWriter();
}
