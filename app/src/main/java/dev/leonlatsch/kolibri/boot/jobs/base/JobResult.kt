package dev.leonlatsch.kolibri.boot.jobs.base

/**
 * Result object for jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class JobResult<T> {

    var isSuccessful: Boolean = false

    var result: T? = null

    constructor() {}

    constructor(successful: Boolean, result: T) {
        this.isSuccessful = successful
        this.result = result
    }
}
