package org.hamit.batchdemo.batch;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BatchConstants {
    public static final String DEFAULT_BATCH_CONFIG_PREFIX = "default";
    public static final String ORDER_PRODUCT_JOB_NAME = "orderProductJob";
    public static final String ORDER_PRODUCT_JOB_STEP = "orderProductStep";
}
