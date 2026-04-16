package com.dreamer.matholympappv1.domain.usecase.auth;

import android.util.Log;

import com.dreamer.matholympappv1.data.repository.FirebaseAuthRepository;
import com.dreamer.matholympappv1.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

/**
 * UseCase для обновления токена аутентификации.
 * 
 * Этот класс реализует механизм refresh token для Firebase Authentication.
 * 
 * Как это работает:
 * 1. Firebase автоматически обновляет ID токен каждые 55 минут
 * 2. Токен действителен в течение 1 часа
 * 3. При истечении срока действия Firebase SDK автоматически получает новый токен
 * 4. Для принудительного обновления можно вызвать с forceRefresh=true
 * 
 * Когда нужно вызывать refresh:
 * - Перед важными API запросами (если токен старый)
 * - При получении ошибки аутентификации от сервера
 * - Периодически в фоновом режиме (каждые 50-55 минут)
 */
public class RefreshTokenUseCase {
    
    private static final String TAG = "RefreshTokenUseCase";
    
    private final AuthRepository authRepository;
    
    public RefreshTokenUseCase() {
        this.authRepository = new FirebaseAuthRepository();
    }
    
    /**
     * Обновляет токен пользователя.
     * 
     * @param forceRefresh Если true, токен будет запрошен заново с сервера.
     *                     Если false, будет возвращен кэшированный токен (если он еще валиден).
     * @param callback Результат операции
     */
    public void execute(boolean forceRefresh, OnTokenRefreshedCallback callback) {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        
        if (currentUser == null) {
            Log.w(TAG, "Пользователь не авторизован, невозможно обновить токен");
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        Log.d(TAG, "Начало обновления токена для пользователя: " + currentUser.getEmail());
        
        authRepository.getIdToken(forceRefresh, new AuthRepository.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.i(TAG, "Токен успешно получен (длина: " + (token != null ? token.length() : 0) + ")");
                
                // Здесь можно сохранить токен в зашифрованном хранилище если нужно
                // Например, для отправки на бэкенд
                
                callback.onTokenRefreshed(token);
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Ошибка при получении токена: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }
    
    /**
     * Проверяет необходимость обновления токена и обновляет при необходимости.
     * 
     * Firebase SDK автоматически управляет токенами, но этот метод можно использовать
     * для проверки перед критическими операциями.
     * 
     * @param callback Результат операции
     */
    public void refreshTokenIfNeeded(OnTokenRefreshedCallback callback) {
        // Firebase SDK автоматически обновляет токены когда они истекают
        // Этот метод просто запрашивает текущий токен (без принудительного обновления)
        execute(false, callback);
    }
    
    /**
     * Принудительно обновляет токен.
     * Используется когда известно что токен устарел или скомпрометирован.
     * 
     * @param callback Результат операции
     */
    public void forceRefresh(OnTokenRefreshedCallback callback) {
        execute(true, callback);
    }
    
    /**
     * Callback для результата обновления токена.
     */
    public interface OnTokenRefreshedCallback {
        void onTokenRefreshed(String newToken);
        void onError(String errorMessage);
    }
}
