package org.hamit.batchdemo.batch;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BatchConstants {
    public static final String DEFAULT_BATCH_CONFIG_PREFIX = "default";

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class JobNames {
        public static final String ORDER_PRODUCT_JOB = "orderProductJob";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StepNames {
        public static final String ORDER_PRODUCT_STEP = "orderProductStep";
    }

}
