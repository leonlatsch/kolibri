package dev.leonlatsch.olivia.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import dev.leonlatsch.olivia.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView version = findViewById(R.id.info_version);

        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText(String.format("%s%s", getString(R.string.version_prefix), packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            finish();
        }
    }
}
