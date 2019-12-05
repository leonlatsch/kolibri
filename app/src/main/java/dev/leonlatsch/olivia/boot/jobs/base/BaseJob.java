package dev.leonlatsch.olivia.boot.jobs.base;

import android.content.Context;

public abstract class BaseJob {

    private Context context;

    protected BaseJob(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
