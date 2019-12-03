package dev.leonlatsch.olivia.boot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.boot.jobs.CheckUserAsyncJob;
import dev.leonlatsch.olivia.boot.jobs.UpdateContactsAsyncJob;
import dev.leonlatsch.olivia.boot.jobs.ValidateBackendAsyncJob;
import dev.leonlatsch.olivia.database.interfaces.UserInterface;
import dev.leonlatsch.olivia.login.LoginActivity;
import dev.leonlatsch.olivia.main.MainActivity;
import dev.leonlatsch.olivia.rest.service.RestServiceFactory;

public class BootActivity extends AppCompatActivity {

    private UserInterface userInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);

        ActiveAndroid.initialize(this);

        userInterface = UserInterface.getInstance();

        new Handler().postDelayed(() -> { // Delay execution for 100 ms to show splash screen
            new ValidateBackendAsyncJob(this).execute(jobResult -> {
                if (jobResult.isSuccessful()) {
                    RestServiceFactory.initialize(this);
                    CheckUserAsyncJob job = new CheckUserAsyncJob(this);
                    job.execute(userResult -> new Handler(getApplicationContext().getMainLooper()).post(() -> {
                        if (jobResult.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            new UpdateContactsAsyncJob(this).execute(null);
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                        finish();
                    }));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setView(getLayoutInflater().inflate(R.layout.dialog_backend, null));
                    builder.create().show();
                }
            });
        }, 100);
    }
}
