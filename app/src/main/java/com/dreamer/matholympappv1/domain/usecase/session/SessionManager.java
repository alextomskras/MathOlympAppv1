package com.dreamer.matholympappv1.domain.usecase.session;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dreamer.matholympappv1.data.repository.FirebaseAuthRepository;
import com.dreamer.matholympappv1.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Менеджер сессий для управления временем жизни пользовательской сессии
 * и автоматическим обновлением токенов.
 * 
 * Этот класс реализует механизм refresh token с периодическим обновлением:
 * - Firebase ID токен действителен 1 час
 * - Firebase SDK автоматически обновляет токен каждые 55 минут
 * - Этот менеджер принудительно обновляет токен каждые 50 минут для надежности
 * - При обнаружении проблем с сессией уведомляет слушателей
 */
public class SessionManager {
    
    private static final String TAG = "SessionManager";
    private static final long TOKEN_REFRESH_INTERVAL_MS = 50 * 60 * 1000; // 50 минут
    private static final long INITIAL_DELAY_MS = 5 * 60 * 1000; // Первая проверка через 5 минут
    
    private final AuthRepository authRepository;
    private final ScheduledExecutorService scheduler;
    private final Handler mainHandler;
    private SessionStateListener stateListener;
    private boolean isRunning;
    
    public SessionManager() {
        this.authRepository = new FirebaseAuthRepository();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.isRunning = false;
    }
    
    /**
     * Запускает менеджер сессий с автоматическим обновлением токена.
     * Вызывать после успешного входа пользователя.
     */
    public void start() {
        if (isRunning) {
            Log.w(TAG, "Менеджер сессий уже запущен");
            return;
        }
        
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Невозможно запустить менеджер сессий: пользователь не авторизован");
            return;
        }
        
        Log.d(TAG, "Запуск менеджера сессий для пользователя: " + currentUser.getEmail());
        isRunning = true;
        
        // Планируем периодическое обновление токена
        scheduler.scheduleAtFixedRate(
                this::refreshTokenPeriodically,
                INITIAL_DELAY_MS,
                TOKEN_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
        
        Log.i(TAG, "Периодическое обновление токена запущено (интервал: 50 минут)");
    }
    
    /**
     * Останавливает менеджер сессий.
     * Вызывать при выходе пользователя или уничтожении приложения.
     */
    public void stop() {
        if (!isRunning) {
            Log.w(TAG, "Менеджер сессий уже остановлен");
            return;
        }
        
        Log.d(TAG, "Остановка менеджера сессий");
        isRunning = false;
        scheduler.shutdown();
        
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        Log.i(TAG, "Менеджер сессий остановлен");
    }
    
    /**
     * Периодическое обновление токена.
     */
    private void refreshTokenPeriodically() {
        if (!isRunning) {
            return;
        }
        
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Пользователь не авторизован, пропускаем обновление токена");
            stop();
            return;
        }
        
        Log.d(TAG, "Периодическое обновление токена для: " + currentUser.getEmail());
        
        authRepository.getIdToken(true, new AuthRepository.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.i(TAG, "Токен успешно обновлен");
                
                if (stateListener != null) {
                    mainHandler.post(() -> stateListener.onTokenRefreshed(token));
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Ошибка обновления токена: " + errorMessage);
                
                if (stateListener != null) {
                    mainHandler.post(() -> stateListener.onSessionError(errorMessage));
                }
            }
        });
    }
    
    /**
     * Принудительно обновляет токен вне расписания.
     */
    public void forceRefreshToken(OnTokenRefreshCallback callback) {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            callback.onError("Пользователь не авторизован");
            return;
        }
        
        Log.d(TAG, "Принудительное обновление токена");
        
        authRepository.getIdToken(true, new AuthRepository.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                Log.i(TAG, "Токен успешно обновлен принудительно");
                
                if (callback != null) {
                    mainHandler.post(() -> callback.onTokenRefreshed(token));
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Ошибка принудительного обновления токена: " + errorMessage);
                
                if (callback != null) {
                    mainHandler.post(() -> callback.onError(errorMessage));
                }
            }
        });
    }
    
    /**
     * Проверяет текущее состояние сессии.
     */
    public boolean isSessionActive() {
        return isRunning && authRepository.isSessionValid();
    }
    
    /**
     * Устанавливает слушателя состояния сессии.
     */
    public void setStateListener(SessionStateListener listener) {
        this.stateListener = listener;
    }
    
    /**
     * Удаляет слушателя состояния сессии.
     */
    public void removeStateListener() {
        this.stateListener = null;
    }
    
    /**
     * Callback для уведомления об изменении состояния сессии.
     */
    public interface SessionStateListener {
        void onTokenRefreshed(String newToken);
        void onSessionError(String errorMessage);
    }
    
    /**
     * Callback для результата обновления токена.
     */
    public interface OnTokenRefreshCallback {
        void onTokenRefreshed(String newToken);
        void onError(String errorMessage);
    }
}
