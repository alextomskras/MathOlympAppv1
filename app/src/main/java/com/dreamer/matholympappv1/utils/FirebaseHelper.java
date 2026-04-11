package com.dreamer.matholympappv1.utils;

import static android.content.ContentValues.TAG;
import static com.dreamer.matholympappv1.utils.SharedPreffUtils.sharedPreffsSaveHintLimits;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;

public class FirebaseHelper {

    public static void getHintLimitsFromFirebase() {
        MyArrayList.firebaseGetHintLimits(hintlimits -> {
            if (hintlimits != null) {
                // Do something with the solution limits value here
                String hintslimits = String.valueOf(hintlimits);
                Log.d(TAG, "Solution limits3: " + hintlimits);
                Log.d(TAG, "Solution limits31: " + hintslimits);
                sharedPreffsSaveHintLimits(Integer.valueOf(hintslimits));
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


//    public static void getSolutionLimitsFromFirebase() {
//        String solutionslimits = new String();
//        MyArrayList.firebaseGetSolutionLimits(solutionlimits -> {
//
//            if (solutionslimits != null) {
//                // Do something with the solution limits value here
//                solutionslimits = String.valueOf(solutionlimits);
//                Log.d(TAG, "Solution limits2: " + solutionlimits);
//                Log.d(TAG, "Solution limits21: " + solutionslimits);
//                sharedPreffsSaveSolutionLimits(Integer.valueOf(solutionslimits));
//            } else {
//                // Handle the null value here
//                Log.e(TAG, "Hint limits value is null");
//            }
//
//        }, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // Handle the error here
//                Log.e(TAG, "Error getting solution limits from Firebase: " + e.getMessage());
//            }
//        });
//        Log.d(TAG, "Solution limits22: " + solutionslimits);
//
//    }
}
