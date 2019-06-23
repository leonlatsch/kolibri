package de.leonlatsch.olivia.boot;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.leonlatsch.olivia.R;

public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
    }

    private boolean isFirstBoot() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.firstBoot), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(getString(R.string.firstBoot), true)) {
            editor.putBoolean(getString(R.string.firstBoot), false);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }
}
