package dev.leonlatsch.kolibri.boot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.boot.jobs.CheckUserAsyncJob;
import dev.leonlatsch.kolibri.boot.jobs.UpdateContactsAsyncJob;
import dev.leonlatsch.kolibri.boot.jobs.ValidateBackendJob;
import dev.leonlatsch.kolibri.boot.jobs.base.JobResult;
import dev.leonlatsch.kolibri.ui.MainActivity;
import dev.leonlatsch.kolibri.ui.login.LoginActivity;
import dev.leonlatsch.kolibri.rest.service.RestServiceFactory;

/**
 * The first Activity to be started.
 * It runs a bunch of jobs and based of that runs other activities
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        ActiveAndroid.initialize(this);

        new Handler().postDelayed(() -> { // Delay execution for 100 ms to show splash screen
            JobResult<Void> result = new ValidateBackendJob(this).execute();
            if (result.isSuccessful()) {
                RestServiceFactory.initialize(this);
                CheckUserAsyncJob job = new CheckUserAsyncJob(this);
                job.execute(userResult -> new Handler(getApplicationContext().getMainLooper()).post(() -> {
                    if (userResult.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        new UpdateContactsAsyncJob(this).execute(null);
                    } else {
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                    finish();
                }));
            } else { // If there is no backend config show the BackendDialog
                BackendDialog dialog = new BackendDialog(this);
                dialog.setOnDismissListener(dialogInterface -> {
                    startActivity(new Intent(getApplicationContext(), BootActivity.class));
                    finish();
                });
                dialog.show();
            }
        }, 100);
    }
}
