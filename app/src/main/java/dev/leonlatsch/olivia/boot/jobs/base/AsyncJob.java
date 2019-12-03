package dev.leonlatsch.olivia.boot.jobs.base;

import android.content.Context;

public abstract class AsyncJob extends BaseJob {

    private Thread thread;

    public AsyncJob(Context context) {
        super(context);
    }

    public abstract void execute(JobResultCallback jobResultCallback);

    private String getThreadName() {
        return this.getClass().getName() + "-THREAD";
    }

    protected void run(Runnable runnable) {
        thread = new Thread(runnable, getThreadName());
        thread.start();
    }
}
