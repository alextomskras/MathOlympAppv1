package com.dreamer.matholympappv1.ui.ui.scrollingscreen;

import static com.dreamer.matholympappv1.ui.ui.scrollingscreen.ScrollingFragment.TAG;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUserScoreManager {
    static final String BASE_IMAGE_URL = "gs://matholymp1.appspot.com";

    public static void saveUserScore(Integer zadacha_score) {
        String str2 = Integer.toString(zadacha_score);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();
        try {
            DatabaseReference scoreRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("userscore");
            scoreRef.setValue(str2);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user score to Firebase: " + e.getMessage());
        }
    }

    public static void firebaseSaveHintLimits(Integer hintlimits) {
        String str2 = Integer.toString(hintlimits);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();
        try {
            DatabaseReference hintRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("hintlimits");
            hintRef.setValue(str2);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user hintlimits to Firebase: " + e.getMessage());
        }
    }

    public static void firebaseSaveSolutionLimits(Integer solutionlimits) {
        String str2 = Integer.toString(solutionlimits);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "Error: current user is null");
            return;
        }
        String uid = user.getUid();
        try {
            DatabaseReference solutionRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("solutionlimits");
            solutionRef.setValue(str2);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user solutionlimits to Firebase: " + e.getMessage());
        }
    }

    public static void setupFirebaseStorage() {
        FirebaseStorage storageReference = FirebaseStorage.getInstance(BASE_IMAGE_URL);
        StorageReference storageRef = storageReference.getReference();
    }


}
