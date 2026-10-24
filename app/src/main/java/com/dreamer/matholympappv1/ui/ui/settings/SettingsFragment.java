package com.dreamer.matholympappv1.ui.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.dreamer.matholympappv1.MainActivity;
import com.dreamer.matholympappv1.R;
import com.dreamer.matholympappv1.utils.PrefKeys;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private boolean isDarkThemeEnabled;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        isDarkThemeEnabled = sharedPreferences.getBoolean(PrefKeys.DARK_THEME_ENABLED, false);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Preference logoutPref = findPreference("logout");
        if (logoutPref != null) {
            logoutPref.setOnPreferenceClickListener(preference -> {
                showLogoutConfirmationDialog();
                return true;
            });
        }
    }


    private void showLogoutConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", (dialog, which) -> {
                    ((MainActivity) requireActivity()).logout();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PrefKeys.DARK_THEME_ENABLED)) {
            boolean isDarkThemeEnabled = sharedPreferences.getBoolean(key, false);

            // Меняем тему без перезапуска вручную
            AppCompatDelegate.setDefaultNightMode(
                    isDarkThemeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }

    @Override
    public void onDestroyView() {
        // Слушатель снимаем в onDestroyView: там ещё живы preferenceManager и context,
        // на этапе onDestroy getContext() уже может быть null
        if (getPreferenceManager() != null && getPreferenceManager().getSharedPreferences() != null) {
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
        super.onDestroyView();
    }

    public boolean isDarkThemeEnabled() {
        return isDarkThemeEnabled;
    }
}