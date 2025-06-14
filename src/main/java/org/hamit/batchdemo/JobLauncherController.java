package org.hamit.batchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobLauncherController {
    private final JobLauncher jobLauncher;
    private final Job job;

    public JobLauncherController(JobLauncher jobLauncher, @Qualifier("sampleJob") Job job) {
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    @GetMapping("/job1")
    public void runJob() throws Exception{
        jobLauncher.run(job,new JobParameters());
    }

}
