package dev.leonlatsch.olivia.boot.jobs.base;

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

    public abstract void execute(AsyncJobCallback asyncJobCallback);

    private String getThreadName() {
        return this.getClass().getName() + "-THREAD";
    }

    protected void run(Runnable runnable) {
        thread = new Thread(runnable, getThreadName());
        thread.start();
    }
}
