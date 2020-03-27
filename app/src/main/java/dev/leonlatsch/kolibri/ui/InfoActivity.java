package dev.leonlatsch.kolibri.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import dev.leonlatsch.kolibri.BuildConfig;
import dev.leonlatsch.kolibri.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView version = findViewById(R.id.info_version);
        TextView licences = findViewById(R.id.licences_link);

        version.setText(String.format("%s%s", getString(R.string.version_prefix), BuildConfig.VERSION_NAME));

        licences.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.url_third_party)));
            startActivity(intent);
        });
    }
}
