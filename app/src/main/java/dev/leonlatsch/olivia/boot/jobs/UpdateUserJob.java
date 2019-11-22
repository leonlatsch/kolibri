package dev.leonlatsch.olivia.boot.jobs;

import android.content.Context;

public class UpdateUserJob extends Job {

    public UpdateUserJob(Context context) {
        super(context);
    }

    @Override
    void execute(JobResultCallback jobResultCallback) {
        System.out.println("abc");
        jobResultCallback.onResult(new JobResult<String>(true, "abc"));
    }
}
