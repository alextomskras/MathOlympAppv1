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
 * Раньше данных касались три класса-обёртки:
 *   SharedPreffUtils    ("MySharedPreferences", int, статическое поле — падало при NPE),
 *   SharedPreffUtils2   ("MySharedPreferences", int/string/boolean),
 *   SecureSharedPrefsUtils ("secure_shared_prefs", те же данные под другими именами).
 * Они писали в разные файлы, счёт хранился как "zadacha_score" и "user_score",
 * лимиты — как int "solution_limits" и строка "solutionlimits". Значения
 * рассинхронизировались, а при выходе часть хранилищ не очищалась.
 *
 * Теперь всё локальное состояние проходит через этот класс:
 * один файл, зашифрованный (EncryptedSharedPreferences), один набор ключей
 * (см. PrefKeys), все чтения/записи защищены — поле не бывает null.
 *
 * Instance создаётся с ApplicationContext и живёт коротко: SharedPreferences
 * кэшируются системой, объект не keeps Activity и не протекает.
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
            // Fallback к обычным SharedPreferences (менее безопасно, но данные не теряются)
            encrypted = appContext.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        }
        prefs = encrypted;
        migrateFromLegacy(appContext);
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
        if (username == null || uid == null) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PrefKeys.USERNAME, username);
        editor.putString(PrefKeys.UID, uid);
        editor.apply();
    }

    public void saveLoginStatus(boolean isLoggedIn) {
        prefs.edit().putBoolean(PrefKeys.IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean loadLoginStatus() {
        return prefs.getBoolean(PrefKeys.IS_LOGGED_IN, false);
    }

    // --- Лимиты (единственный ключ для всех мест приложения) ---

    public void saveSolutionLimits(int solutionLimits) {
        prefs.edit().putInt(PrefKeys.SOLUTION_LIMITS, solutionLimits).apply();
    }

    public int loadSolutionLimits() {
        return prefs.getInt(PrefKeys.SOLUTION_LIMITS, PrefKeys.DEFAULT_SOLUTION_LIMITS);
    }

    public void saveHintLimits(int hintLimits) {
        prefs.edit().putInt(PrefKeys.HINT_LIMITS, hintLimits).apply();
    }

    public int loadHintLimits() {
        return prefs.getInt(PrefKeys.HINT_LIMITS, PrefKeys.DEFAULT_HINT_LIMITS);
    }

    /** Обе пары значений одним edit(): они не расходятся по времени записи. */
    public void saveLimits(int solutionLimits, int hintLimits) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PrefKeys.SOLUTION_LIMITS, solutionLimits);
        editor.putInt(PrefKeys.HINT_LIMITS, hintLimits);
        editor.apply();
    }

    // --- Счёт ---

    public void saveUserScore(int score) {
        prefs.edit().putInt(PrefKeys.USER_SCORE, score).apply();
    }

    public int loadUserScore() {
        return prefs.getInt(PrefKeys.USER_SCORE, 0);
    }

    /**
     * Полная очистка локальных данных. Используется при выходе из аккаунта.
     * apply() здесь не подходит: следующий экран читает эти значения сразу
     * после навигации, и apply() мог бы не успеть примениться.
     */
    public void clearData() {
        prefs.edit().clear().commit();
    }

    /**
     * Однократный перенос данных со старого слоя в единые ключи.
     * Ставится флаг, поэтому выполняется один раз за установку; старые файлы
     * после переноса очищаются, чтобы их больше никто не читал.
     */
    private void migrateFromLegacy(Context appContext) {
        if (prefs.getBoolean(PrefKeys.MIGRATED_LEGACY, false)) {
            return;
        }
        SharedPreferences legacyPlain = appContext
                .getSharedPreferences(LEGACY_PLAIN_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor out = prefs.edit();

        // Счёт: "zadacha_score" (plain) и "user_score" (secure) -> user_score
        int legacyScore = legacyPlain.getInt(LEGACY_SCORE_KEY, 0);
        int legacySecureScore = prefs.getInt(LEGACY_SECURE_SCORE_KEY, 0);
        int bestScore = Math.max(legacyScore, legacySecureScore);
        if (bestScore > 0 && prefs.getInt(PrefKeys.USER_SCORE, 0) == 0) {
            out.putInt(PrefKeys.USER_SCORE, bestScore);
        }

        // Лимиты: int из plain-файла
        int legacySolution = legacyPlain.getInt(PrefKeys.SOLUTION_LIMITS, -1);
        if (legacySolution >= 0 && !prefs.contains(PrefKeys.SOLUTION_LIMITS)) {
            out.putInt(PrefKeys.SOLUTION_LIMITS, legacySolution);
        }
        int legacyHint = legacyPlain.getInt(PrefKeys.HINT_LIMITS, -1);
        if (legacyHint >= 0 && !prefs.contains(PrefKeys.HINT_LIMITS)) {
            out.putInt(PrefKeys.HINT_LIMITS, legacyHint);
        }

        // Лимиты: строки "solutionlimits"/"hintlimits" из secure-файла
        String legacySolutionStr = prefs.getString(LEGACY_SOLUTION_LIMITS_STR, null);
        if (legacySolutionStr != null && !prefs.contains(PrefKeys.SOLUTION_LIMITS)) {
            int value = parseInt(legacySolutionStr);
            if (value >= 0) {
                out.putInt(PrefKeys.SOLUTION_LIMITS, value);
            }
        }
        String legacyHintStr = prefs.getString(LEGACY_HINT_LIMITS_STR, null);
        if (legacyHintStr != null && !prefs.contains(PrefKeys.HINT_LIMITS)) {
            int value = parseInt(legacyHintStr);
            if (value >= 0) {
                out.putInt(PrefKeys.HINT_LIMITS, value);
            }
        }

        // Идентификация: SharedPreffUtils2 писал username/uid в plain-файл
        String legacyUsername = legacyPlain.getString(PrefKeys.USERNAME, "");
        if (prefs.getString(PrefKeys.USERNAME, "").isEmpty() && !legacyUsername.isEmpty()) {
            out.putString(PrefKeys.USERNAME, legacyUsername);
        }
        String legacyUid = legacyPlain.getString(PrefKeys.UID, "");
        if (prefs.getString(PrefKeys.UID, "").isEmpty() && !legacyUid.isEmpty()) {
            out.putString(PrefKeys.UID, legacyUid);
        }

        out.putBoolean(PrefKeys.MIGRATED_LEGACY, true);
        out.commit();
        // Plain-файл больше никем не читается — чистим, чтобы старые значения
        // не остались на устройстве после миграции.
        legacyPlain.edit().clear().commit();
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
