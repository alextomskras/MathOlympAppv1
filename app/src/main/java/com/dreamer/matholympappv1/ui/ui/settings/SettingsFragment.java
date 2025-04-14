package com.dreamer.matholympappv1.ui.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.dreamer.matholympappv1.MainActivity;
import com.dreamer.matholympappv1.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private boolean isDarkThemeEnabled;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        isDarkThemeEnabled = sharedPreferences.getBoolean("dark_theme_enabled", false);

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
//        if (key.equals("dark_theme_enabled")) {
//            isDarkThemeEnabled = sharedPreferences.getBoolean(key, false);
//            Toast.makeText(getContext(), "R.style.AppTheme_Dark = " + R.style.AppTheme_Dark + ", isDarkThemeEnabled = " + isDarkThemeEnabled, Toast.LENGTH_SHORT).show();
//
//            // Use the boolean value to set the theme
//            if (isDarkThemeEnabled) {
//                getActivity().setTheme(R.style.AppTheme_Dark);
//            } else {
//                getActivity().setTheme(R.style.AppTheme);
//            }
//
//
//            // Recreate the activity to apply the new theme
//            if (getActivity() != null) {
//                getActivity().recreate();
//            }
//            getActivity().recreate();
//        }
//    }

        if (key.equals("dark_theme_enabled")) {
            boolean isDarkThemeEnabled = sharedPreferences.getBoolean(key, false);

            // Меняем тему без перезапуска вручную
            AppCompatDelegate.setDefaultNightMode(
                    isDarkThemeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }

//    private Context getActivity(Object mainActivity) {
//        return getContext();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    public boolean isDarkThemeEnabled() {
        return isDarkThemeEnabled;
    }
}