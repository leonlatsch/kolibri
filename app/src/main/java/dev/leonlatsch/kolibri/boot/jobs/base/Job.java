package dev.leonlatsch.kolibri.boot.jobs.base;

import android.content.Context;

/**
 * Base class for sync jobs
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public abstract class Job extends BaseJob {

    protected Job(Context context) {
        super(context);
    }

    public abstract JobResult execute();
}
