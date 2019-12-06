package dev.leonlatsch.olivia.boot.jobs.base;

import android.content.Context;

/**
 * @author Leon Latsch
 * @since 1.0.0
 */
public abstract class BaseJob {

    private Context context;

    protected BaseJob(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
