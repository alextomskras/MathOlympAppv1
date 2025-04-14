package com.dreamer.matholympappv1.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreffUtils2 {

    private SharedPreferences sharedPreferences;

    // Конструктор для инициализации SharedPreferences
    public SharedPreffUtils2(Context context) {
        this.sharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
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

    // Сохранение данных для пользователя (например, username)
    public void saveUsername(String username) {
        saveString("username", username);
    }

    // Загрузка имени пользователя
    public String loadUsername() {
        return loadString("username");
    }

    public void saveUid(String uid) {
        saveString("uid", uid);
    }

    public String loadUid() {
        return loadString("uid");
    }

    // Универсальное сохранение булевого значения
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Универсальная загрузка булевого значения
    public boolean loadBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void saveLoginStatus(boolean isLoggedIn) {
        saveBoolean("is_logged_in", isLoggedIn);
    }

    public boolean loadLoginStatus() {
        return loadBoolean("is_logged_in", false);
    }

    // Сохранение балла задачи
    public void saveUserScore(int score) {
        saveInt("zadacha_score", score);
    }

    // Загрузка балла задачи
    public int loadUserScore() {
        return loadInt("zadacha_score", 0);
    }

    // Сохранение лимитов для решений
    public void saveSolutionLimits(int solutionLimits) {
        saveInt("solution_limits", solutionLimits);
    }

    // Загрузка лимитов для решений
    public int loadSolutionLimits() {
        return loadInt("solution_limits", 1);  // Default to 1
    }

    // Сохранение лимитов подсказок
    public void saveHintLimits(int hintLimits) {
        saveInt("hint_limits", hintLimits);
    }

    // Загрузка лимитов подсказок
    public int loadHintLimits() {
        return loadInt("hint_limits", 3);  // Default to 3
    }

    // Очистка всех данных
    public void clearData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
