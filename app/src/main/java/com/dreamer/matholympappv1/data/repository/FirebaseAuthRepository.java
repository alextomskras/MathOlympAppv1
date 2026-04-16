package com.dreamer.matholympappv1.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.dreamer.matholympappv1.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация репозитория аутентификации для работы с Firebase Auth.
 * Управляет пользовательскими сессиями, токенами и их обновлением.
 */
public class FirebaseAuthRepository implements AuthRepository {
    
    private static final String TAG = "FirebaseAuthRepo";
    private static final long TOKEN_REFRESH_THRESHOLD_MS = 5 * 60 * 1000; // 5 минут
    
    private final FirebaseAuth mAuth;
    private final List<AuthStateListener> authStateListeners;
    private final FirebaseAuth.AuthStateListener firebaseAuthListener;
    
    public FirebaseAuthRepository() {
        this.mAuth = FirebaseAuth.getInstance();
        this.authStateListeners = new ArrayList<>();
        
        // Создаем слушателя изменений состояния Firebase
        this.firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Log.d(TAG, "Состояние аутентификации изменилось: " + (user != null ? user.getEmail() : "null"));
            
            // Уведомляем всех слушателей
            for (AuthStateListener listener : authStateListeners) {
                listener.onAuthStateChanged(user);
            }
        };
        
        // Регистрируем слушателя
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    
    @Override
    public void login(String email, String password, AuthCallback callback) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            callback.onError("Email и пароль не могут быть пустыми");
            return;
        }
        
        Log.d(TAG, "Попытка входа для пользователя: " + email);
        
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.i(TAG, "Вход успешен: " + user.getEmail());
                        callback.onSuccess(user);
                    } else {
                        String errorMessage = task.getException() != null 
                                ? task.getException().getMessage() 
                                : "Неизвестная ошибка";
                        Log.e(TAG, "Ошибка входа: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    @Override
    public void register(String email, String password, AuthCallback callback) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            callback.onError("Email и пароль не могут быть пустыми");
            return;
        }
        
        Log.d(TAG, "Попытка регистрации для пользователя: " + email);
        
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.i(TAG, "Регистрация успешна: " + user.getEmail());
                        callback.onSuccess(user);
                    } else {
                        String errorMessage = task.getException() != null 
                                ? task.getException().getMessage() 
                                : "Неизвестная ошибка";
                        Log.e(TAG, "Ошибка регистрации: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    @Override
    public void logout(AuthCallback callback) {
        Log.d(TAG, "Выход пользователя");
        
        try {
            mAuth.signOut();
            Log.i(TAG, "Выполнен выход из системы");
            callback.onSuccess(null);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при выходе: " + e.getMessage());
            callback.onError(e.getMessage());
        }
    }
    
    @Override
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    
    @Override
    public void refreshToken(boolean forceRefresh, TokenCallback callback) {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        Log.d(TAG, "Обновление токена (forceRefresh=" + forceRefresh + ")");
        
        user.getIdToken(forceRefresh)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        GetTokenResult result = task.getResult();
                        String idToken = result.getToken();
                        Log.i(TAG, "Токен успешно обновлен");
                        callback.onTokenReceived(idToken);
                    } else {
                        String errorMessage = task.getException() != null 
                                ? task.getException().getMessage() 
                                : "Неизвестная ошибка";
                        Log.e(TAG, "Ошибка обновления токена: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }
    
    @Override
    public void getIdToken(boolean forceRefresh, TokenCallback callback) {
        refreshToken(forceRefresh, callback);
    }
    
    @Override
    public boolean isSessionValid() {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            return false;
        }
        
        // Проверяем, не истек ли токен
        // Firebase автоматически управляет валидностью сессии,
        // но мы можем проверить наличие пользователя
        return true;
    }
    
    @Override
    public void addAuthStateListener(AuthStateListener listener) {
        if (listener != null && !authStateListeners.contains(listener)) {
            authStateListeners.add(listener);
            Log.d(TAG, "Добавлен слушатель состояния аутентификации");
        }
    }
    
    @Override
    public void removeAuthStateListener(AuthStateListener listener) {
        if (listener != null && authStateListeners.contains(listener)) {
            authStateListeners.remove(listener);
            Log.d(TAG, "Удален слушатель состояния аутентификации");
        }
    }
    
    /**
     * Освобождает ресурсы при уничтожении репозитория.
     */
    public void onDestroy() {
        mAuth.removeAuthStateListener(firebaseAuthListener);
        authStateListeners.clear();
        Log.d(TAG, "Репозиторий аутентификации уничтожен");
    }
}
