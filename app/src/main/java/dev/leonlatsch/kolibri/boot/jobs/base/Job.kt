package dev.leonlatsch.kolibri.boot.jobs.base

import android.content.Context

/**
 * Base class for sync jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
abstract class Job protected constructor(context: Context) : BaseJob(context) {

    abstract fun execute(): JobResult
}
