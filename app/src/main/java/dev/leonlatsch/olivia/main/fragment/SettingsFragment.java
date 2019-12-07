package dev.leonlatsch.olivia.main.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dev.leonlatsch.olivia.R;
import dev.leonlatsch.olivia.settings.Config;

/**
 * Fragment for the setting
 *
 * @author Leon Latsch
 * @since 1.0.0
 */
public class SettingsFragment extends Fragment {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        preferences = Config.getSharedPreferences(getContext());
        editor = preferences.edit();

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
