package com.dreamer.matholympappv1.utils;

import android.app.Activity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class UserEmailLoginFirebase {
    public static void updateUiWithUser(String username, String password, Activity activity) {
        String username1 = username;
        String password1 = password;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(username1, password1).addOnCompleteListener(activity,
                task -> {
                    if (task.isSuccessful()) {
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                task.getResult().getUser().getEmail(), Snackbar.LENGTH_LONG).show();
//                        Bundle args = new Bundle();
//                        args.putString("username", username);
//                        args.putString("password", password);
//                        navController.clearBackStack(R.id.loginFragment);
//                        navController.navigate(R.id.action_loginFragment_to_zadachaFragment);
                    } else {
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }
}