package dev.leonlatsch.olivia.boot.jobs;

import android.content.Context;

public abstract class Job {

    private Thread thread;

    private Context context;

    public Job(Context context) {
        this.context = context;
    }

    abstract void execute(JobResultCallback jobResultCallback);

    protected Thread getThread() {
        return thread;
    }

    protected String getThreadName() {
        return this.getClass().getName() + "-THREAD";
    }
}
