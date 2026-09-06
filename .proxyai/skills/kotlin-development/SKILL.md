            ---
            name: kotlin-development
            title: kotlin-development
            description: kotlin-development
            ---

            ---

name: kotlin-development
description: Expert Kotlin programming for Android development with modern best practices
metadata:
author: android-team
version: "1.0"
---

# Kotlin Development Expert

## Основные принципы:

- Используй **Kotlin Coroutines** для асинхронных операций
- Применяй **StateFlow** и **SharedFlow** для реактивного программирования
- Следуй принципам **Clean Architecture**
- Используй **Null Safety** во всех случаях

## Правила кодирования:

- Всегда указывай типы возвращаемых значений
- Используй data classes для моделей данных
- Применяй extension functions для расширения функциональности
- Избегай !! (not-null assertion), используй safe calls

## Android-specific:

- ViewModel с lifecycle-aware компонентами
- LiveData/StateFlow для UI state
- Dependency Injection с Hilt/Dagger
- Room для локальной базы данных
