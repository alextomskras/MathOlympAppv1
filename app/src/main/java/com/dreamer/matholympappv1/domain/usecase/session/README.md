# Session Management & Refresh Token

## Обзор

Этот пакет содержит компоненты для управления пользовательскими сессиями и автоматического обновления токенов аутентификации.

## Компоненты

### SessionManager
Основной класс для управления сессией пользователя:
- Автоматическое обновление токена каждые 50 минут
- Принудительное обновление по требованию
- Уведомление слушателей об изменении состояния сессии

### RefreshTokenUseCase
UseCase для разового обновления токена:
- `execute(forceRefresh, callback)` - обновить токен
- `refreshTokenIfNeeded(callback)` - обновить если нужен
- `forceRefresh(callback)` - принудительное обновление

## Как использовать

### В LoginFragment после успешного входа:

```java
// После успешного логина
SessionManager sessionManager = new SessionManager();
sessionManager.setStateListener(new SessionManager.SessionStateListener() {
    @Override
    public void onTokenRefreshed(String newToken) {
        // Токен обновлен, можно отправить на сервер если нужно
        Log.d("Session", "Новый токен получен");
    }
    
    @Override
    public void onSessionError(String errorMessage) {
        // Ошибка сессии, возможно требуется повторный вход
        Log.e("Session", "Ошибка: " + errorMessage);
    }
});
sessionManager.start();

// Сохраняем sessionManager в поле фрагмента или ViewModel
```

### В Fragment onStop/onDestroy:

```java
@Override
public void onStop() {
    super.onStop();
    if (sessionManager != null) {
        sessionManager.stop();
    }
}
```

### Для принудительного обновления перед важным запросом:

```java
RefreshTokenUseCase refreshTokenUseCase = new RefreshTokenUseCase();
refreshTokenUseCase.forceRefresh(new RefreshTokenUseCase.OnTokenRefreshedCallback() {
    @Override
    public void onTokenRefreshed(String newToken) {
        // Используем новый токен для API запроса
        sendRequestToServer(newToken);
    }
    
    @Override
    public void onError(String errorMessage) {
        // Обработка ошибки
    }
});
```

## Жизненный цикл токена Firebase

1. **ID Token** действителен 1 час
2. **Refresh Token** бессрочный (пока пользователь не выйдет)
3. Firebase SDK автоматически обновляет ID токен каждые 55 минут
4. Наш SessionManager обновляет каждые 50 минут для надежности

## Когда происходит обновление

- Каждые 50 минут автоматически
- При запуске приложения (если сессия активна)
- Перед критическими операциями (по требованию)
- При получении ошибки аутентификации от сервера

## Безопасность

- Токены хранятся в памяти, не сохраняются в SharedPreferences
- Firebase сам управляет безопасностью токенов
- EncryptedSharedPreferences используется только для metadata (UID, email)
- При выходе все токены уничтожаются
