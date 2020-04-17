package dev.leonlatsch.kolibri.boot.jobs.base;

import android.content.Context;

/**
 * Base class for async jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public abstract class AsyncJob extends BaseJob {

    private Thread thread;

    public AsyncJob(Context context) {
        super(context);
    }

    protected abstract void run(AsyncJobCallback asyncJobCallback);

    public void execute(AsyncJobCallback asyncJobCallback) {
        Runnable runnable = () -> run(asyncJobCallback);
        thread = new Thread(runnable, getThreadName());
        thread.start();
    }

    private String getThreadName() {
        return this.getClass().getName() + "-THREAD";
    }
}
