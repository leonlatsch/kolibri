package dev.leonlatsch.olivia.main.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import dev.leonlatsch.olivia.R;

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
    }
}
