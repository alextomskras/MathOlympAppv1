package com.dreamer.matholympappv1.ui.main.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.dreamer.matholympappv1.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}