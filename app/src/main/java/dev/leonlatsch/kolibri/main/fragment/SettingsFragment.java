package dev.leonlatsch.kolibri.main.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import dev.leonlatsch.kolibri.R;
import dev.leonlatsch.kolibri.settings.Config;

/**
 * Fragment for the setting
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        findPreference(Config.KEY_BACKEND_HTTP_BASEURL).setSummary(Config.getSharedPreferences(getActivity()).getString(Config.KEY_BACKEND_HTTP_BASEURL, ""));
    }
}
