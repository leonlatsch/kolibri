package dev.leonlatsch.kolibri.boot.jobs.base

/**
 * Callback interface for async jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
interface AsyncJobCallback {

    fun onResult(jobResult: JobResult<Any?>)
}
