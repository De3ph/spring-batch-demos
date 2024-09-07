package org.hamit.batchdemo;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class JobLauncherController {
    private JobLauncher jobLauncher;
    private Job job;

    @GetMapping("/job1")
    public void runJob() throws Exception {
        jobLauncher.run(job, new JobParameters());
    }
}
