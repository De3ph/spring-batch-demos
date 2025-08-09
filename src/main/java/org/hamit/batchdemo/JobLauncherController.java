package org.hamit.batchdemo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hamit.batchdemo.batch.BatchConstants;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringQualifierCopyableLombok")
@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor(onConstructor_ = @__({@Autowired}))
@Tag(name = "Batch Jobs", description = "API for managing batch job executions")
public class JobLauncherController {

    private final JobOperator jobOperator;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;

    @Qualifier(value = BatchConstants.ORDER_PRODUCT_JOB_NAME)
    private final Job orderProductBatch;

    @GetMapping("/order-job")
    @Operation(
            summary = "Execute Order Product Batch Job",
            description = "Starts the execution of the order product batch processing job"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job started successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while starting the job")
    })
    public ResponseEntity<String> runOrderJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(orderProductBatch, jobParameters);

            return ResponseEntity.ok("Job started with execution ID: " + jobExecution.getId());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to start job: " + e.getMessage());
        }
    }

    @GetMapping("/job-status")
    @Operation(
            summary = "Get Job Status",
            description = "Retrieves the status of the latest job executions"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job status retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred while retrieving job status")
    })
    public ResponseEntity<String> getJobStatus() {
        try {
            var jobInstances = jobExplorer.getJobInstances(BatchConstants.ORDER_PRODUCT_JOB_NAME, 0, 10);
            if (jobInstances.isEmpty()) {
                return ResponseEntity.ok("No job instances found");
            }

            var latestInstance = jobInstances.get(0);
            var executions = jobExplorer.getJobExecutions(latestInstance);

            if (executions.isEmpty()) {
                return ResponseEntity.ok("No job executions found");
            }

            var latestExecution = executions.iterator().next();
            return ResponseEntity.ok("Latest job status: " + latestExecution.getStatus() +
                    ", Exit Status: " + latestExecution.getExitStatus().getExitCode());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to get job status: " + e.getMessage());
        }
    }
}
