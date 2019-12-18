package dev.leonlatsch.olivia.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import dev.leonlatsch.olivia.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView version = findViewById(R.id.info_version);
        TextView licences = findViewById(R.id.licences_link);

        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version.setText(String.format("%s%s", getString(R.string.version_prefix), packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            finish();
        }

        licences.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.url_third_party)));
            startActivity(intent);
        });
    }
}
