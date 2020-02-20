package dev.leonlatsch.kolibri.main.fragment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference

import androidx.preference.PreferenceFragmentCompat

import dev.leonlatsch.kolibri.R
import dev.leonlatsch.kolibri.main.MainActivity
import dev.leonlatsch.kolibri.settings.Config

/**
 * Fragment for the setting
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        findPreference<Preference>(Config.KEY_BACKEND_HTTP_BASEURL)?.summary = Config.getSharedPreferences(activity as MainActivity).getString(Config.KEY_BACKEND_HTTP_BASEURL, "")
    }
}
