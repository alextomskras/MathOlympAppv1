package com.dreamer.matholympappv1.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;

public class FirebaseHelper {

    /**
     * Лимиты записываются в единое хранилище (UserPrefsRepository), ключ
     * hint_limits — тот же, что читает ScrollingFragment. Метод статический,
     * поэтому репозиторий передаётся параметром.
     */
    public static void getHintLimitsFromFirebase(UserPrefsRepository prefs) {
        MyArrayList.firebaseGetHintLimits(hintlimits -> {
            if (hintlimits != null) {
                // Do something with the solution limits value here
                String hintslimits = String.valueOf(hintlimits);
                Log.d(TAG, "Solution limits3: " + hintlimits);
                Log.d(TAG, "Solution limits31: " + hintslimits);
                prefs.saveHintLimits(Integer.valueOf(hintslimits));
            } else {
                // Handle the null value here
                Log.e(TAG, "Hint limits value is null");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle the error here
                Log.e(TAG, "Error getting solution limits from Firebase: " + e.getMessage());
            }
        });
    }
}
