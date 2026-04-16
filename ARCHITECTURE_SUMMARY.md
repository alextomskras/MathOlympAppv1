# Архитектурное резюме: Refresh Token и разделение слоев

## 📁 Новая структура проекта

```
app/src/main/java/com/dreamer/matholympappv1/
├── domain/                          # 🔵 Domain Layer (Бизнес-логика)
│   ├── repository/
│   │   └── AuthRepository.java      # Интерфейс репозитория аутентификации
│   └── usecase/
│       ├── auth/
│       │   ├── LoginUseCase.java    # Бизнес-логика входа
│       │   ├── LogoutUseCase.java   # Бизнес-логика выхода
│       │   └── RefreshTokenUseCase.java  # Обновление токена
│       └── session/
│           └── SessionManager.java  # Управление сессией и refresh token
│
├── data/                            # 🟢 Data Layer (Данные и инфраструктура)
│   ├── model/                       # Модели данных
│   ├── repository/
│   │   └── FirebaseAuthRepository.java  # Реализация AuthRepository
│   └── ...
│
├── ui/                              # 🟡 UI Layer (Presentation)
│   └── ui/
│       ├── login/                   # MVVM для входа
│       ├── register/                # MVVM для регистрации
│       └── ...
│
└── utils/                           # ⚪ Utilities (общие утилиты)
    ├── SecureSharedPrefsUtils.java  # ✅ Оставить здесь
    ├── InputValidator.java          # ✅ Оставить здесь
    └── UserEmailLoginFirebase.java  # ❌ Удалить (устарел)
```

## 🔑 Ключевые изменения

### 1. Выделен Domain Layer
- **Раньше**: Бизнес-логика была размазана между UI и Utils
- **Теперь**: Четкое разделение - domain содержит бизнес-правила, use cases

### 2. Repository Pattern
- **AuthRepository** (интерфейс в domain) - определяет контракт
- **FirebaseAuthRepository** (реализация в data) - конкретная реализация для Firebase

### 3. UseCase Pattern
Каждый UseCase инкапсулирует одно бизнес-действие:
- `LoginUseCase` - вход с валидацией и маппингом ошибок
- `LogoutUseCase` - выход с очисткой сессии
- `RefreshTokenUseCase` - обновление токена

### 4. Session Manager
- Автоматическое обновление токена каждые 50 минут
- Принудительное обновление по требованию
- Слушатели состояния сессии

## 🔄 Как работает Refresh Token

```
┌─────────────────────────────────────────────────────────────┐
│                    Пользователь входит                       │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              LoginUseCase.execute(email, password)           │
│  - Валидация email/password                                  │
│  - Вызов AuthRepository.login()                              │
│  - Маппинг ошибок Firebase → пользовательские сообщения      │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│         FirebaseAuthRepository.login() → Firebase Auth       │
│  - signInWithEmailAndPassword()                              │
│  - Получение FirebaseUser                                    │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              Запуск SessionManager.start()                   │
│  - Планирование авто-обновления каждые 50 минут              │
│  - Сохранение UID/email в EncryptedSharedPreferences         │
└─────────────────────────────────────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            │                               │
            ▼                               ▼
┌───────────────────────┐       ┌─────────────────────────────┐
│  Каждые 50 минут:     │       │  Перед важным запросом:     │
│  Авто-обновление      │       │  forceRefreshToken()        │
│  токена               │       │                             │
└───────────────────────┘       └─────────────────────────────┘
            │                               │
            └───────────────┬───────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│         FirebaseAuthRepository.getIdToken(forceRefresh)      │
│  - user.getIdToken(true)                                     │
│  - Получение нового ID токена от Firebase                    │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                  Использование токена                        │
│  - Отправка на бэкенд (если нужно)                           │
│  - Авторизация API запросов                                  │
└─────────────────────────────────────────────────────────────┘
```

## 📋 Примеры использования

### В LoginFragment (после успешного входа)

```java
private SessionManager sessionManager;

private void performFirebaseLogin(String email, String password) {
    LoginUseCase loginUseCase = new LoginUseCase();
    
    loginUseCase.execute(email, password, new LoginUseCase.OnLoginCompleteCallback() {
        @Override
        public void onLoginComplete(FirebaseUser user) {
            // Успешный вход
            
            // 1. Сохраняем данные в зашифрованное хранилище
            sharedPrefs.saveLoginStatus(true);
            sharedPrefs.saveUsername(email);
            sharedPrefs.saveUid(user.getUid());
            
            // 2. Запускаем менеджер сессий
            sessionManager = new SessionManager();
            sessionManager.setStateListener(new SessionManager.SessionStateListener() {
                @Override
                public void onTokenRefreshed(String newToken) {
                    Log.d(TAG, "Токен обновлен автоматически");
                    // Можно отправить новый токен на сервер если нужно
                }
                
                @Override
                public void onSessionError(String errorMessage) {
                    Log.e(TAG, "Ошибка сессии: " + errorMessage);
                    // Возможно потребуется повторный вход
                }
            });
            sessionManager.start();
            
            // 3. Навигация на главный экран
            navigateToMainScreen(email);
        }
        
        @Override
        public void onError(String errorMessage) {
            // Показываем ошибку пользователю
            showLoginError(errorMessage);
        }
    });
}

@Override
public void onStop() {
    super.onStop();
    if (sessionManager != null) {
        sessionManager.stop();
    }
}
```

### В RAZDELFragment (перед важным запросом)

```java
private void submitSolutionToServer(String solution) {
    // Принудительно обновляем токен перед отправкой
    RefreshTokenUseCase refreshTokenUseCase = new RefreshTokenUseCase();
    
    refreshTokenUseCase.forceRefresh(new RefreshTokenUseCase.OnTokenRefreshedCallback() {
        @Override
        public void onTokenRefreshed(String newToken) {
            // Используем свежий токен для запроса
            sendSolutionWithToken(solution, newToken);
        }
        
        @Override
        public void onError(String errorMessage) {
            // Обработка ошибки
            showTokenRefreshError();
        }
    });
}
```

### При выходе из системы

```java
signoutButton.setOnClickListener(v -> {
    LogoutUseCase logoutUseCase = new LogoutUseCase(() -> {
        // Очистка локальных данных
        sharedPrefs.clearData();
        
        // Остановка менеджера сессий
        if (sessionManager != null) {
            sessionManager.stop();
            sessionManager = null;
        }
    });
    
    logoutUseCase.execute(new LogoutUseCase.OnLogoutCompleteCallback() {
        @Override
        public void onLogoutComplete() {
            // Навигация на экран входа
            navigateToLogin();
        }
        
        @Override
        public void onError(String errorMessage) {
            // Даже при ошибке очищаем локальные данные
            sharedPrefs.clearData();
            navigateToLogin();
        }
    });
});
```

## ✅ Преимущества новой архитектуры

### Безопасность
- ✅ Пароли не хранятся и не передаются между экранами
- ✅ Токены обновляются автоматически
- ✅ EncryptedSharedPreferences для чувствительных данных
- ✅ Четкое разделение ответственности

### Поддерживаемость
- ✅ Бизнес-логика изолирована в domain слое
- ✅ Легко тестировать UseCase отдельно от UI
- ✅ Можно заменить Firebase на другую систему аутентификации
- ✅ Понятная структура проекта

### Масштабируемость
- ✅ Легко добавлять новые UseCase
- ✅ Repository pattern позволяет абстрагироваться от источника данных
- ✅ SessionManager легко расширить новыми функциями

## 🎯 Что было улучшено

| Компонент | Было | Стало |
|-----------|------|-------|
| **Хранение пароля** | Поле в LoggedInUser | ❌ Удалено |
| **Передача пароля** | Через Bundle между Fragment | ❌ Удалено |
| **Обновление токена** | Отсутствовало | ✅ SessionManager (50 мин) |
| **Бизнес-логика** | Размазана по UI/Utils | ✅ Выделена в domain/usecase |
| **Repository** | Фиктивные DataSource | ✅ FirebaseAuthRepository |
| **Обработка ошибок** | Прямые сообщения Firebase | ✅ Маппинг на пользовательские |

## 📝 Следующие шаги

1. **Интегрировать SessionManager в LoginFragment**
2. **Удалить устаревшие классы** (UserEmailLoginFirebase, LoginDataSource)
3. **Удалить поле password из LoggedInUser**
4. **Добавить Dependency Injection** (Hilt/Koin) для управления зависимостями
5. **Покрыть Unit-тестами** domain слой (UseCase)
6. **Добавить обработку истечения сессии** (logout при-refresh token failed)
