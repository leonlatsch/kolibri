package dev.leonlatsch.olivia.boot.jobs;

public abstract class Job {

    private Thread thread;

    abstract void execute(JobResultCallback jobResultCallback);

    protected Thread getThread() {
        return thread;
    }

    protected String getThreadName() {
        return this.getClass().getName() + "-THREAD";
    }
}
