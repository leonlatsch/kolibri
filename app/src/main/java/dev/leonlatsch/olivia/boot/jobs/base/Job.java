package dev.leonlatsch.olivia.boot.jobs.base;

import android.content.Context;

public abstract class Job {

    private Thread thread;

    private Context context;

    public Job(Context context) {
        this.context = context;
    }

    public abstract void execute(JobResultCallback jobResultCallback);

    private String getThreadName() {
        return this.getClass().getName() + "-THREAD";
    }

    public Context getContext() {
        return context;
    }

    protected void run(Runnable runnable) {
        thread = new Thread(runnable, getThreadName());
        thread.start();
    }
}
