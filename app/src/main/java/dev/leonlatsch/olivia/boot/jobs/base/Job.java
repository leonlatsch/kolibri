package dev.leonlatsch.olivia.boot.jobs.base;

import android.content.Context;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public abstract class Job extends BaseJob{

    protected Job(Context context) {
        super(context);
    }

    public abstract JobResult execute();
}
