package com.dreamer.matholympappv1.domain.usecase.auth;

import android.util.Log;

import com.dreamer.matholympappv1.data.repository.FirebaseAuthRepository;
import com.dreamer.matholympappv1.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

/**
 * UseCase для выхода пользователя из системы.
 * Реализует бизнес-логику завершения сессии и очистки данных.
 */
public class LogoutUseCase {
    
    private static final String TAG = "LogoutUseCase";
    
    private final AuthRepository authRepository;
    private final OnSessionClearedCallback sessionClearedCallback;
    
    public LogoutUseCase(OnSessionClearedCallback sessionClearedCallback) {
        this.authRepository = new FirebaseAuthRepository();
        this.sessionClearedCallback = sessionClearedCallback;
    }
    
    /**
     * Выполняет выход пользователя из системы.
     * 
     * @param callback Результат операции
     */
    public void execute(OnLogoutCompleteCallback callback) {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        
        if (currentUser == null) {
            Log.w(TAG, "Пользователь уже не авторизован");
            // Все равно вызываем очистку локальных данных
            clearLocalSession();
            callback.onLogoutComplete();
            return;
        }
        
        String userEmail = currentUser.getEmail();
        Log.d(TAG, "Выполнение выхода для пользователя: " + userEmail);
        
        authRepository.logout(new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.i(TAG, "Выход успешен: " + userEmail);
                
                // Очищаем локальные данные сессии
                clearLocalSession();
                
                callback.onLogoutComplete();
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Ошибка при выходе: " + errorMessage);
                
                // Даже если Firebase signOut не удался, очищаем локальные данные
                // чтобы пользователь не остался в заблокированном состоянии
                clearLocalSession();
                
                callback.onError(errorMessage);
            }
        });
    }
    
    /**
     * Очищает локальные данные сессии (SharedPreferences, кэш и т.д.).
     */
    private void clearLocalSession() {
        Log.d(TAG, "Очистка локальных данных сессии");
        
        if (sessionClearedCallback != null) {
            sessionClearedCallback.onSessionCleared();
        }
    }
    
    /**
     * Проверяет активна ли сессия пользователя.
     */
    public boolean isUserLoggedIn() {
        return authRepository.isSessionValid();
    }
    
    /**
     * Callback для очистки локальной сессии.
     */
    public interface OnSessionClearedCallback {
        void onSessionCleared();
    }
    
    /**
     * Callback для результата выхода.
     */
    public interface OnLogoutCompleteCallback {
        void onLogoutComplete();
        void onError(String errorMessage);
    }
}
