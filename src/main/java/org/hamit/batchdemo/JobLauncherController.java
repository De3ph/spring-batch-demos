package org.hamit.batchdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobLauncherController {
    private final JobLauncher jobLauncher;
    @Qualifier("firstJob")
    private final Job job;

    @GetMapping("/job1")
    public void runJob() throws Exception{
        jobLauncher.run(job,new JobParameters());
    }
}
