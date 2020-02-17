package dev.leonlatsch.kolibri.boot.jobs.base;

/**
 * Result object for jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class JobResult<T> {

    private boolean successful;

    private T result;

    public JobResult() {
    }

    public JobResult(boolean successful, T result) {
        this.successful = successful;
        this.result = result;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
