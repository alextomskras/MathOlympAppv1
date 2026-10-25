package com.dreamer.matholympappv1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Единственное локальное хранилище приложения.
 *
 * Один зашифрованный файл, один набор ключей (PrefKeys), все чтения и записи
 * защищены: поле prefs не бывает null, пара username+uid пишется одним редактором,
 * лимиты — одним edit().
 *
 * Миграция старых значений (PR #62) читает legacy-ключи типобезопасно: старые
 * обёртки писали лимиты то как int ("solution_limits"), то как String
 * ("solutionlimits"), поэтому getInt() на таком ключе бросает ClassCastException.
 * Здесь тип определяется чтением (getInt → fallback на getString), и старые
 * ключи удаляются из файла после переноса, чтобы crash не повторился.
 */
public class UserPrefsRepository {

    private static final String TAG = "UserPrefsRepository";
    private static final String PREF_FILE_NAME = "secure_shared_prefs";

    /** Legacy-имена, которые нужно один раз перенести в единые ключи. */
    private static final String LEGACY_PLAIN_FILE = "MySharedPreferences";
    private static final String LEGACY_SCORE_KEY = "zadacha_score";
    private static final String LEGACY_SECURE_SCORE_KEY = "user_score";
    private static final String LEGACY_SOLUTION_LIMITS_STR = "solutionlimits";
    private static final String LEGACY_HINT_LIMITS_STR = "hintlimits";

    private final SharedPreferences prefs;

    public UserPrefsRepository(Context context) {
        Context appContext = context.getApplicationContext();
        SharedPreferences encrypted;
        try {
            MasterKey masterKey = new MasterKey.Builder(appContext)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            encrypted = EncryptedSharedPreferences.create(
                    appContext,
                    PREF_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Не удалось создать EncryptedSharedPreferences: " + e.getMessage());
            // Fallback к обычным SharedPreferences (менее безопасно, но данные не теряются).
            // Приложение должно предупредить пользователя, что приватные данные
            // лежат открытым текстом (SettingsFragment/диалог).
            encrypted = appContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        }
        prefs = encrypted;
        try {
            migrateFromLegacy(appContext);
        } catch (Exception e) {
            // Миграция не должна ронять приложение: данные останутся в старом виде.
            Log.e(TAG, "Миграция локальных данных не выполнена: " + e.getMessage(), e);
        }
    }

    // --- Идентификация пользователя ---

    public void saveUsername(String username) {
        if (username == null) {
            return;
        }
        prefs.edit().putString(PrefKeys.USERNAME, username).apply();
    }

    public String loadUsername() {
        return prefs.getString(PrefKeys.USERNAME, "");
    }

    public void saveUid(String uid) {
        if (uid == null) {
            return;
        }
        prefs.edit().putString(PrefKeys.UID, uid).apply();
    }

    public String loadUid() {
        return prefs.getString(PrefKeys.UID, "");
    }

    /**
     * username и uid пишутся одним edit() — пара не может разойтись:
     * раньше uid записывался в асинхронном колбэке после записи username,
     * и экран мог показывать имя прошлого пользователя с новым uid.
     */
    public void saveUsernameAndUid(String username, String uid) {
        SharedPreferences.Editor editor = prefs.edit();
        if (username != null) {
            editor.putString(PrefKeys.USERNAME, username);
        }
        if (uid != null) {
            editor.putString(PrefKeys.UID, uid);
        }
        editor.apply();
    }

    // --- Статус логина (используется LoginFragment) ---

    public void saveLoginStatus(boolean isLoggedIn) {
        prefs.edit().putBoolean(PrefKeys.IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean loadLoginStatus() {
        return prefs.getBoolean(PrefKeys.IS_LOGGED_IN, false);
    }

    // --- Лимиты и счёт ---

    public void saveSolutionLimits(int value) {
        prefs.edit().putInt(PrefKeys.SOLUTION_LIMITS, value).apply();
    }

    public int loadSolutionLimits() {
        return prefs.getInt(PrefKeys.SOLUTION_LIMITS, PrefKeys.DEFAULT_SOLUTION_LIMITS);
    }

    public void saveHintLimits(int value) {
        prefs.edit().putInt(PrefKeys.HINT_LIMITS, value).apply();
    }

    public int loadHintLimits() {
        return prefs.getInt(PrefKeys.HINT_LIMITS, PrefKeys.DEFAULT_HINT_LIMITS);
    }

    /** Лимиты пишутся одним edit(): значения не расходятся. */
    public void saveLimits(int solutionLimits, int hintLimits) {
        prefs.edit()
                .putInt(PrefKeys.SOLUTION_LIMITS, solutionLimits)
                .putInt(PrefKeys.HINT_LIMITS, hintLimits)
                .apply();
    }

    public void saveUserScore(int score) {
        prefs.edit().putInt(PrefKeys.USER_SCORE, score).apply();
    }

    public int loadUserScore() {
        return prefs.getInt(PrefKeys.USER_SCORE, 0);
    }

    /**
     * Очистка локальной сессии при выходе. commit(), а не apply():
     * выход завершается навигацией и очисткой, данные должны исчезнуть
     * до того, как процесс будет убит.
     */
    public void clearData() {
        prefs.edit()
                .remove(PrefKeys.USERNAME)
                .remove(PrefKeys.UID)
                .remove(PrefKeys.IS_LOGGED_IN)
                .remove(PrefKeys.SOLUTION_LIMITS)
                .remove(PrefKeys.HINT_LIMITS)
                .remove(PrefKeys.USER_SCORE)
                .commit();
    }

    // --- Миграция ---

    private void migrateFromLegacy(Context appContext) {
        if (prefs.getBoolean(PrefKeys.MIGRATED_LEGACY, false)) {
            return;
        }

        SharedPreferences legacyPlain =
                appContext.getSharedPreferences(LEGACY_PLAIN_FILE, Context.MODE_PRIVATE);

        // Счёт: приоритет каноническому ключу, затем secure-наследию, затем plain.
        int score = readInt(prefs, PrefKeys.USER_SCORE, -1);
        if (score < 0) {
            score = readInt(prefs, LEGACY_SECURE_SCORE_KEY, -1);
        }
        if (score < 0) {
            score = readInt(legacyPlain, LEGACY_SCORE_KEY, -1);
        }
        if (score < 0) {
            score = readInt(legacyPlain, PrefKeys.USER_SCORE, -1);
        }

        // Лимиты решений: int "solution_limits" (обе старые обёртки), затем
        // строка "solutionlimits" из SecureSharedPrefsUtils.
        int solutionLimits = readInt(prefs, PrefKeys.SOLUTION_LIMITS, -1);
        if (solutionLimits < 0) {
            solutionLimits = readInt(legacyPlain, PrefKeys.SOLUTION_LIMITS, -1);
        }
        if (solutionLimits < 0) {
            solutionLimits = readInt(legacyPlain, LEGACY_SOLUTION_LIMITS_STR, -1);
        }

        int hintLimits = readInt(prefs, PrefKeys.HINT_LIMITS, -1);
        if (hintLimits < 0) {
            hintLimits = readInt(legacyPlain, PrefKeys.HINT_LIMITS, -1);
        }
        if (hintLimits < 0) {
            hintLimits = readInt(legacyPlain, LEGACY_HINT_LIMITS_STR, -1);
        }

        SharedPreferences.Editor out = prefs.edit();
        if (score >= 0) {
            out.putInt(PrefKeys.USER_SCORE, score);
        }
        out.putInt(PrefKeys.SOLUTION_LIMITS,
                solutionLimits >= 0 ? solutionLimits : PrefKeys.DEFAULT_SOLUTION_LIMITS);
        out.putInt(PrefKeys.HINT_LIMITS,
                hintLimits >= 0 ? hintLimits : PrefKeys.DEFAULT_HINT_LIMITS);

        // Идентификация: SharedPreffUtils2 писал username/uid в plain-файл.
        String legacyUsername = legacyPlain.getString(PrefKeys.USERNAME, "");
        if (prefs.getString(PrefKeys.USERNAME, "").isEmpty() && !legacyUsername.isEmpty()) {
            out.putString(PrefKeys.USERNAME, legacyUsername);
        }
        String legacyUid = legacyPlain.getString(PrefKeys.UID, "");
        if (prefs.getString(PrefKeys.UID, "").isEmpty() && !legacyUid.isEmpty()) {
            out.putString(PrefKeys.UID, legacyUid);
        }

        // Старые строковые ключи удаляем: после миграции getInt() на канонических
        // ключах больше не встречает String. Ключ "user_score" НЕ удаляем — под ним
        // SecureSharedPrefsUtils писал тот же канонический счёт, remove его стёр бы.
        out.remove(LEGACY_SOLUTION_LIMITS_STR);
        out.remove(LEGACY_HINT_LIMITS_STR);

        out.putBoolean(PrefKeys.MIGRATED_LEGACY, true);
        out.commit();

        // Plain-файл больше никем не читается — чистим, чтобы старые значения
        // не остались на устройстве после миграции.
        legacyPlain.edit().clear().commit();
    }

    /**
     * Типобезопасное чтение int: если значение записано как String,
     * ClassCastException перехватывается и строка парсится.
     */
    private int readInt(SharedPreferences p, String key, int defaultValue) {
        if (!p.contains(key)) {
            return defaultValue;
        }
        try {
            return p.getInt(key, defaultValue);
        } catch (ClassCastException e) {
            try {
                int parsed = parseInt(p.getString(key, null));
                return parsed >= 0 ? parsed : defaultValue;
            } catch (Exception ex) {
                Log.e(TAG, "readInt: ключ " + key + " не читается как int/строка", ex);
                return defaultValue;
            }
        }
    }

    private int parseInt(String s) {
        if (s == null) {
            return -1;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
