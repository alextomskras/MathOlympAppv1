            ---
            name: android-architecture
            title: android-architecture
            description: android-architecture
            ---

            ---

name: android-architecture
description: Clean Architecture and MVVM/MVI patterns for Android
metadata:
author: android-team
version: "1.0"
---

# Android Architecture Expert

## Clean Architecture слои:

### Domain Layer:

- Entities (чистый Kotlin/Java, без Android зависимостей)
- Use Cases (бизнес-логика)
- Repository interfaces

### Data Layer:

- Repository implementations
- Data sources (Remote/Local)
- Models (DTOs, Entity mappers)

### Presentation Layer:

- ViewModels (UI logic)
- UI Controllers (Activity/Fragment/Composable)
- UI State management

## Dependency Injection:

- Hilt для Android
- @Inject, @Provides, @Module
- Scoped bindings (@Singleton, @ViewModelScoped)
