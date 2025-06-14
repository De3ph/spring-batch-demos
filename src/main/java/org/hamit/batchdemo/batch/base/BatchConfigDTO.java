package org.hamit.batchdemo.batch.base;

public record BatchConfigDTO(
        Integer corePoolSize,
        Integer maxPoolSize,
        Integer queueCapacity) {
}
