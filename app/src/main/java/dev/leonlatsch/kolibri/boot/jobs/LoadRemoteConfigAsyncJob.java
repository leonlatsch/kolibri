package dev.leonlatsch.kolibri.boot.jobs;

import android.content.Context;

import java.io.IOException;
import java.util.HashMap;

import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJob;
import dev.leonlatsch.kolibri.boot.jobs.base.AsyncJobCallback;
import dev.leonlatsch.kolibri.boot.jobs.base.JobResult;
import dev.leonlatsch.kolibri.rest.dto.Container;
import dev.leonlatsch.kolibri.rest.service.ConfigService;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;
import dev.leonlatsch.kolibri.settings.RemoteConfig;
import retrofit2.Response;

/**
 *
 */
public class LoadRemoteConfigAsyncJob extends AsyncJob {

    private ConfigService configService;

    public LoadRemoteConfigAsyncJob(Context context) {
        super(context);
        configService = RestServiceFactory.getConfigService();
    }

    @Override
    protected void run(AsyncJobCallback asyncJobCallback) {
        JobResult<Void> result = new JobResult<>();
        result.setResult(null);
        try {
            Response<Container<HashMap<String, Object>>> res = configService.get().execute();
            if (res.isSuccessful()) {
                RemoteConfig.getInstance().setConfig(res.body().getContent());
                result.setSuccessful(true);
            } else {
                result.setSuccessful(false);
            }
        } catch (IOException e) {
            result.setSuccessful(false);
        }
        if (asyncJobCallback != null) {
            asyncJobCallback.onResult(result);
        }
    }
}
