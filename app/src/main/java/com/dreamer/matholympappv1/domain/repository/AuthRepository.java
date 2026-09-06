package com.dreamer.matholympappv1.domain.repository;

import com.google.firebase.auth.FirebaseUser;

/**
 * Интерфейс репозитория для операций аутентификации.
 * Определяет контракт для работы с пользовательскими сессиями и токенами.
 */
public interface AuthRepository {
    
    /**
     * Выполняет вход пользователя по email и паролю.
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @param callback Результат операции
     */
    void login(String email, String password, AuthCallback callback);
    
    /**
     * Регистрирует нового пользователя.
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @param callback Результат операции
     */
    void register(String email, String password, AuthCallback callback);
    
    /**
     * Выполняет выход из системы.
     * @param callback Результат операции
     */
    void logout(AuthCallback callback);
    
    /**
     * Проверяет текущую сессию и при необходимости обновляет токен.
     * @return Текущий пользователь или null если сессия не активна
     */
    FirebaseUser getCurrentUser();
    
    /**
     * Принудительно обновляет ID токен пользователя.
     * Вызывается когда токен устарел или требуется свежий токен для API запросов.
     * @param forceRefresh Если true, токен будет запрошен заново с сервера
     * @param callback Результат операции с новым токеном
     */
    void refreshToken(boolean forceRefresh, TokenCallback callback);
    
    /**
     * Проверяет валидность текущей сессии.
     * @return true если сессия активна и токен валиден
     */
    boolean isSessionValid();
    
    /**
     * Получает текущий ID токен пользователя.
     * @param forceRefresh Если true, токен будет запрошен заново с сервера
     * @param callback Результат операции с токеном
     */
    void getIdToken(boolean forceRefresh, TokenCallback callback);
    
    /**
     * Слушатель состояния аутентификации.
     * Вызывается при изменении состояния входа/выхода.
     */
    interface AuthStateListener {
        void onAuthStateChanged(FirebaseUser user);
    }
    
    /**
     * Добавляет слушателя изменения состояния аутентификации.
     */
    void addAuthStateListener(AuthStateListener listener);
    
    /**
     * Удаляет слушателя изменения состояния аутентификации.
     */
    void removeAuthStateListener(AuthStateListener listener);
    
    /**
     * Callback для операций аутентификации.
     */
    interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String errorMessage);
    }
    
    /**
     * Callback для операций с токенами.
     */
    interface TokenCallback {
        void onTokenReceived(String token);
        void onError(String errorMessage);
    }
}
