package dev.leonlatsch.olivia.boot.jobs;

import android.content.Context;

public abstract class Job {

    private Thread thread;

    private Context context;

    public Job(Context context) {
        this.context = context;
    }

    public abstract void execute(JobResultCallback jobResultCallback);

    public void postExecute() {
    }

    protected Thread getThread() {
        return thread;
    }

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
