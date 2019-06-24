package de.leonlatsch.olivia.boot;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.leonlatsch.olivia.R;
import de.leonlatsch.olivia.constants.Values;

public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
    }

    private boolean isFirstBoot() {
        SharedPreferences sharedPreferences = getSharedPreferences(Values.PREF_FIRST_BOOT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(Values.PREF_FIRST_BOOT, true)) {
            editor.putBoolean(Values.PREF_FIRST_BOOT, false);
            editor.apply();
            return true;
        } else {
            return false;
        }
    }
}
