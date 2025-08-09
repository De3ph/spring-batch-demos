package org.hamit.batchdemo;

import lombok.RequiredArgsConstructor;
import org.hamit.batchdemo.batch.BatchConstants;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringQualifierCopyableLombok")
@RestController
@RequiredArgsConstructor(onConstructor_ = @__({@Autowired}))
public class JobLauncherController {

    private final JobOperator jobOperator;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;

    @Qualifier(value = BatchConstants.ORDER_PRODUCT_JOB_NAME)
    private final Job orderProductBatch;

    @GetMapping("/orderJob")
    public void runJob() throws Exception {
        jobLauncher.run(orderProductBatch,new JobParameters());
    }

}
