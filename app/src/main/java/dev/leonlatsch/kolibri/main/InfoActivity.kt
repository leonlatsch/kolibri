package dev.leonlatsch.kolibri.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import dev.leonlatsch.kolibri.BuildConfig
import dev.leonlatsch.kolibri.R

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val version = findViewById<TextView>(R.id.info_version)
        val licences = findViewById<TextView>(R.id.licences_link)

        version.setText(String.format("%s%s", getString(R.string.version_prefix), BuildConfig.VERSION_NAME))

        licences.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.url_third_party))
            startActivity(intent)
        }
    }
}
