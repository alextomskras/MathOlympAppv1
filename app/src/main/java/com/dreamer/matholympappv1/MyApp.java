package com.dreamer.matholympappv1;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Enable Firebase Database persistence
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d(TAG, "Firebase Database persistence enabled");
        } catch (Exception e) {
            Log.e(TAG, "Failed to enable Firebase Database persistence", e);
        }

        // Инициализация других сервисов, если необходимо
        // initializeOtherServices();
    }

    // Пример метода для инициализации других сервисов
    private void initializeOtherServices() {
        // Инициализация других сервисов
        // Например, инициализация аналитики, рекламы и т.д.
    }
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        // Enable Firebase Database persistence
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//    }
}