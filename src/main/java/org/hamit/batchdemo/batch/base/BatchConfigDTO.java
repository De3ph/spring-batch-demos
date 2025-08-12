package org.hamit.batchdemo.batch.base;

public record BatchConfigDTO(
        int corePoolSize,
        int maxPoolSize,
        int queueCapacity,
        int chunkSize
) {
}
