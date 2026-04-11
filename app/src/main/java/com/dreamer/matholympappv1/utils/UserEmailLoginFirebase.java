package com.dreamer.matholympappv1.utils;

import android.app.Activity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class UserEmailLoginFirebase {
    public static void updateUiWithUser(String username, String password, Activity activity) {
        // Получаем переданные значения имени пользователя и пароля
        String username1 = username;
        String password1 = password;

        // Получаем экземпляр FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Выполняем вход в систему с использованием переданных имени пользователя и пароля
        mAuth.signInWithEmailAndPassword(username1, password1).addOnCompleteListener(activity,
                task -> {
                    if (task.isSuccessful()) {
                        // Если вход в систему успешен, показываем Snackbar с электронной почтой пользователя
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                task.getResult().getUser().getEmail(), Snackbar.LENGTH_LONG).show();

                        // Здесь можно добавить дополнительный код для обработки успешного входа в систему
                        // Например, переход на другой экран или выполнение других действий

//                        Bundle args = new Bundle();
//                        args.putString("username", username);
//                        args.putString("password", password);
//                        navController.clearBackStack(R.id.loginFragment);
//                        navController.navigate(R.id.action_loginFragment_to_zadachaFragment);

                    } else {
                        // Если вход в систему не удался, показываем Snackbar с сообщением об ошибке
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

                        // Здесь можно добавить дополнительный код для обработки неудачного входа в систему
                        // Например, показать диалоговое окно с предупреждением или выполнить другие действия
                    }
                });
    }
}