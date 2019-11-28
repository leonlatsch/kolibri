package dev.leonlatsch.olivia.boot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.activeandroid.ActiveAndroid;

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.boot.jobs.CheckUserJob;
import dev.leonlatsch.olivia.boot.jobs.UpdateContactsJob;
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
        RestServiceFactory.initialize(this);

        userInterface = UserInterface.getInstance();

        new Handler().postDelayed(() -> { // Delay execution for 100 ms to show splash screen
            userInterface.loadUser();

            CheckUserJob job = new CheckUserJob(this);
            job.execute(jobResult -> new Handler(getApplicationContext().getMainLooper()).post(() -> {
                if (jobResult.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    new UpdateContactsJob(this).execute(null);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                finish();
            }));
        }, 100);
    }
}
