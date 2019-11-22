package dev.leonlatsch.olivia.boot.jobs;

public class JobResult<T> {

    private final boolean successful;

    private final T result;

    public JobResult(boolean successful, T result) {
        this.successful = successful;
        this.result = result;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public T getResult() {
        return result;
    }
}
