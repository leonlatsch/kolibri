package dev.leonlatsch.kolibri.boot.jobs.base;

import android.content.Context;

/**
 * Base class for jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public abstract class BaseJob {

    private Context context;

    protected BaseJob(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
