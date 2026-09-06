package com.dreamer.matholympappv1.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Безопасный класс для работы с SharedPreferences с использованием шифрования.
 * Использует EncryptedSharedPreferences для автоматического шифрования данных
 * с ключами из аппаратного хранилища Android Keystore.
 */
public class SecureSharedPrefsUtils {
    
    private static final String PREF_FILE_NAME = "secure_shared_prefs";
    private static final String TAG = "SecureSharedPrefs";
    
    private SharedPreferences sharedPreferences;
    
    /**
     * Конструктор для инициализации EncryptedSharedPreferences
     */
    public SecureSharedPrefsUtils(Context context) {
        try {
            // Создаем MasterKey для шифрования
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            
            // Создаем зашифрованные SharedPreferences
            this.sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            
            Log.d(TAG, "EncryptedSharedPreferences успешно инициализированы");
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Ошибка инициализации EncryptedSharedPreferences: " + e.getMessage());
            // Fallback к обычным SharedPreferences (менее безопасно)
            this.sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        }
    }
    
    // Сохранение строки
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    
    // Загрузка строки
    public String loadString(String key) {
        return sharedPreferences.getString(key, "");
    }
    
    // Сохранение целого числа
    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    
    // Загрузка целого числа
    public int loadInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }
    
    // Сохранение булевого значения
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    
    // Загрузка булевого значения
    public boolean loadBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    
    // Сохранение имени пользователя
    public void saveUsername(String username) {
        saveString("username", username);
    }
    
    // Загрузка имени пользователя
    public String loadUsername() {
        return loadString("username");
    }
    
    // Сохранение UID пользователя
    public void saveUid(String uid) {
        saveString("uid", uid);
    }
    
    // Загрузка UID пользователя
    public String loadUid() {
        return loadString("uid");
    }
    
    // Сохранение статуса входа
    public void saveLoginStatus(boolean isLoggedIn) {
        saveBoolean("is_logged_in", isLoggedIn);
    }
    
    // Загрузка статуса входа
    public boolean loadLoginStatus() {
        return loadBoolean("is_logged_in", false);
    }
    
    // Сохранение баллов пользователя
    public void saveUserScore(int score) {
        saveInt("user_score", score);
    }
    
    // Загрузка баллов пользователя
    public int loadUserScore() {
        return loadInt("user_score", 0);
    }
    
    // Сохранение лимита решений
    public void saveSolutionLimits(int solutionLimits) {
        saveInt("solution_limits", solutionLimits);
    }
    
    // Загрузка лимита решений
    public int loadSolutionLimits() {
        return loadInt("solution_limits", 1);
    }
    
    // Сохранение лимита подсказок
    public void saveHintLimits(int hintLimits) {
        saveInt("hint_limits", hintLimits);
    }
    
    // Загрузка лимита подсказок
    public int loadHintLimits() {
        return loadInt("hint_limits", 3);
    }
    
    // Очистка всех данных
    public void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
