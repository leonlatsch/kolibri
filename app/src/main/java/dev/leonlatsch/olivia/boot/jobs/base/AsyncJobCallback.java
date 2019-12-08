package dev.leonlatsch.olivia.boot.jobs.base;

/**
 * Callback interface for async jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public interface AsyncJobCallback {

    void onResult(JobResult jobResult);
}
