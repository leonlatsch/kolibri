package dev.leonlatsch.olivia.boot.jobs;

import android.content.Context;
import android.content.SharedPreferences;

import dev.leonlatsch.olivia.boot.jobs.base.Job;
import dev.leonlatsch.olivia.boot.jobs.base.JobResult;
import dev.leonlatsch.olivia.boot.jobs.base.JobResultCallback;
import dev.leonlatsch.olivia.rest.service.CommonService;
import dev.leonlatsch.olivia.settings.Config;

import static dev.leonlatsch.olivia.settings.Config.*;

public class ValidateBackendJob extends Job {

    private SharedPreferences preferences;

    private CommonService commonService;

    public ValidateBackendJob(Context context) {
        super(context);
        preferences = Config.getSharedPreferences(context);
    }

    @Override
    public void execute(JobResultCallback jobResultCallback) {
        run(() -> {
            String baseUrl = preferences.getString(KEY_BACKEND_HTTP_BASEURL, null);
            String brokerHost = preferences.getString(KEY_BACKEND_BROKER_HOST, null);
            int brokerPort = preferences.getInt(KEY_BACKEND_BROKER_PORT, 0);

            if (baseUrl == null || brokerHost == null || brokerPort == 0) {
                jobResultCallback.onResult(new JobResult<Void>(false, null));
            } else {
                jobResultCallback.onResult(new JobResult<Void>(true, null));
            }
        });
    }
}
