package dev.leonlatsch.olivia.boot.jobs.base;

import android.content.Context;

public abstract class Job extends BaseJob{

    private Context context;

    protected Job(Context context) {
        super(context);
    }

    public abstract JobResult execute();
}
