package com.dreamer.matholympappv1.domain.usecase.auth;

import android.util.Log;

import com.dreamer.matholympappv1.data.repository.FirebaseAuthRepository;
import com.dreamer.matholympappv1.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

/**
 * UseCase для входа пользователя.
 * Реализует бизнес-логику аутентификации с валидацией данных и обработкой ошибок.
 */
public class LoginUseCase {
    
    private static final String TAG = "LoginUseCase";
    
    private final AuthRepository authRepository;
    
    public LoginUseCase() {
        this.authRepository = new FirebaseAuthRepository();
    }
    
    /**
     * Выполняет вход пользователя по email и паролю.
     * 
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @param callback Результат операции
     */
    public void execute(String email, String password, OnLoginCompleteCallback callback) {
        // Валидация входных данных
        if (!isValidEmail(email)) {
            Log.w(TAG, "Неверный формат email: " + email);
            callback.onError("Неверный формат email");
            return;
        }
        
        if (password == null || password.length() < 6) {
            Log.w(TAG, "Пароль слишком короткий");
            callback.onError("Пароль должен содержать минимум 6 символов");
            return;
        }
        
        Log.d(TAG, "Выполнение входа для пользователя: " + email);
        
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.i(TAG, "Вход успешен: " + user.getEmail());
                callback.onLoginComplete(user);
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Ошибка входа: " + errorMessage);
                
                // Маппинг ошибок Firebase на пользовательские сообщения
                String userFriendlyMessage = mapFirebaseError(errorMessage);
                callback.onError(userFriendlyMessage);
            }
        });
    }
    
    /**
     * Проверяет текущую сессию пользователя.
     * Возвращает пользователя если сессия активна.
     */
    public FirebaseUser checkCurrentSession() {
        return authRepository.getCurrentUser();
    }
    
    /**
     * Простая валидация email формата.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Преобразует ошибки Firebase в понятные пользователю сообщения.
     */
    private String mapFirebaseError(String errorMessage) {
        if (errorMessage == null) {
            return "Неизвестная ошибка";
        }
        
        if (errorMessage.contains("WRONG_PASSWORD") || errorMessage.contains("wrong-password")) {
            return "Неверный пароль";
        }
        if (errorMessage.contains("USER_NOT_FOUND") || errorMessage.contains("user-not-found")) {
            return "Пользователь не найден";
        }
        if (errorMessage.contains("INVALID_EMAIL") || errorMessage.contains("invalid-email")) {
            return "Неверный email";
        }
        if (errorMessage.contains("USER_DISABLED") || errorMessage.contains("user-disabled")) {
            return "Аккаунт отключен";
        }
        if (errorMessage.contains("NETWORK_REQUEST_FAILED") || errorMessage.contains("network")) {
            return "Ошибка сети. Проверьте подключение к интернету.";
        }
        if (errorMessage.contains("TOO_MANY_REQUESTS") || errorMessage.contains("too-many-requests")) {
            return "Слишком много попыток входа. Попробуйте позже.";
        }
        
        return errorMessage;
    }
    
    /**
     * Callback для результата входа.
     */
    public interface OnLoginCompleteCallback {
        void onLoginComplete(FirebaseUser user);
        void onError(String errorMessage);
    }
}
