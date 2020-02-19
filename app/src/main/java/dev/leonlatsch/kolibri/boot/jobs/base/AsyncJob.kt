package dev.leonlatsch.kolibri.boot.jobs.base

import android.content.Context

/**
 * Base class for async jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
abstract class AsyncJob(context: Context) : BaseJob(context) {

    private var thread: Thread? = null

    private val threadName: String
        get() = this.getClass().getName() + "-THREAD"

    abstract fun execute(asyncJobCallback: AsyncJobCallback)

    protected fun run(runnable: Runnable) {
        thread = Thread(runnable, threadName)
        thread!!.start()
    }
}
