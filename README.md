# MathOlympAppv1

Android app for mathematics-olympiad practice: sections (razdel) of problems, answers/solutions,
per-user score and daily limits. Backend is Firebase (Auth, Realtime Database, Storage, Messaging);
task images can be served from a self-hosted MinIO bucket through a signed FastAPI proxy.

## 📱 Features

- **Authentication**: email/password sign-in and registration via Firebase Auth (UseCase + Repository pattern).
- **Sections and problems**: `Razdel` / `Zadachi` nodes in Realtime Database drive the section list and the problem screens.
- **Scoring and limits**: per-user score, solution limits and hint limits, kept in one encrypted local store.
- **Problem images**: answers and solutions load from Firebase Storage, or from MinIO through the signed proxy when it is configured (see [MinIO proxy](#minio-proxy-for-task-images)).
- **Session management**: singleton `SessionManager` refreshes the Firebase ID token every 50 minutes.
- **Settings screen**: dark-theme flag, logout, MinIO proxy URL / secret / enable switch.
- **Splash screen** on launch (androidx core-splashscreen), Material Design UI, ViewBinding, Navigation component.

## 🏗️ Architecture

Layered: UI → Domain → Data, plus a single local storage.

### Local storage — `UserPrefsRepository` + `PrefKeys`

All local state goes through one class (`utils/UserPrefsRepository.java`): one file,
`EncryptedSharedPreferences` (fallback to plain SharedPreferences if the keystore fails), one key
namespace (`utils/PrefKeys.java`). Every read and write is guarded — the prefs field is never null.

The three legacy wrappers (`SharedPreffUtils`, `SharedPreffUtils2`, `SecureSharedPrefsUtils`) are
removed. They wrote to different files with mismatched keys (`zadacha_score` vs `user_score`,
`solution_limits` as int vs `solutionlimits` as String), so values drifted apart and limits reset on
every section switch. Migration reads legacy keys type-safely (int first, then String), deletes them
after the copy, and does not throw the app on a bad key. Score, username, uid, login status and limits
are written with single `edit()` calls, so pairs of values cannot diverge.

### Session and navigation

- `SessionManager` is a lazy singleton — earlier every screen created its own instance, and `stop()` in
  one place killed token refresh for the others. `stop()` calls `shutdownNow()` immediately: it runs on
  the main thread (Exit menu item, sign-out button), and `awaitTermination(5s)` froze the UI.
- Logout is non-blocking: `MainActivity.logout()` sets `isLoggingOut`, dismisses the no-internet dialog,
  stops the singleton, clears local prefs, signs out, and removes the auth-state listener before
  re-adding it, so anonymous sign-in cannot log the user straight back in.
- `RAZDELFragment` → login uses one `navigate()` with `popUpTo(startDestination, inclusive)`. The old
  `clearBackStack(RAZDEL)` + `navigate(action_RAZDEL_to_login)` pair threw `IllegalStateException`
  ("action not owned by current destination"): clearing the back stack removed the destination that
  owned the action. Back on the login screen closes the app.
- `loginFragment` is registered as a top-level destination in `AppBarConfiguration`, so the login screen
  shows no up arrow.
- Dialogs created in `ScrollingFragment` are dismissed in `onDestroyView` — no leaked window on rotation
  or logout, no dialog hanging over the login screen.
- ViewModels do not hold `Context`: the `private Context context` field is removed from
  `ScrollingFragmentViewModel`, context is passed per call, and logs write `uid` instead of email
  (email is personal data in logcat).

### Layers

- **Domain**: `AuthRepository` contract, `LoginUseCase`, `LogoutUseCase`, `RefreshTokenUseCase`, `SessionManager`.
- **Data**: `FirebaseAuthRepository`; models `Users`, `Zadachi`, `Razdel`.
- **UI**: splash, login, register, mainscreen, razdel (sections), scrollingscreen (answers/solutions), zadachascreen, settings.
- **Utils**: `UserPrefsRepository`, `PrefKeys`, `MinioProxyImageLoader`, `FirebaseHelper`, `UserEmailLoginFirebase`, `InputValidator`, `NetworkManager`, `PermissionManager`, `MyArrayList`, `StringIntegerConverter`, action-bar helpers, Kotlin ViewModel extensions.

## 🖼️ MinIO proxy for task images

Off by default: images load from Firebase Storage (`answersimages/…`). When the proxy is enabled in
Settings, `ScrollingFragment` routes the image request through
`ScrollingFragmentViewModel.setRemoteImage()` instead.

Firebase RTDB does not store global task numbers (`Zadachi/<section>/zadacha_N` — numbering is local to
the section), so the client computes the global number: offset = number of problems in every section
whose trailing key digit is smaller than the current section's, plus the task number inside the section.
The section number is the trailing digit of the section key (`razdel_3` → 3). Objects in MinIO are flat:

```
tasks/<global>/answer.jpg
tasks/<global>/solution.jpg
```

Request contract (client → proxy): `POST` to the configured URL with header
`X-Auth: <unix-seconds>:<hex HMAC-SHA256(secret, ts + "\n" + body)>` and body
`{"global": N, "kind": "answer" | "solution"}`. The proxy returns `{"url": <presigned GET, 1 hour>}` and
Coil loads it; unsigned POSTs are rejected. Reference server: `proxy/fastapi_minio_proxy.py`;
`proxy/cron_ping.py` sends a signed `POST /ping` every minute so Render does not sleep the container;
`proxy/upload_images.py` migrates existing images using the same global-number formula as the client.
Client and server signatures are pinned by the unit test `MinioProxyHmacTest` (vectors from
`proxy/gen_vectors.py`).

Full details (server env, cron line, limits of the scheme): [README-MINIO.md](README-MINIO.md).

## 🔐 Firebase rules

Deployment rules are kept in the repo for reference:

- `firebase-rules/database-rules.json` — `Razdel` and `Zadachi` readable by any authenticated user, write
  closed; each `Users/<uid>` node readable and writable only by its own uid.
- `firebase-rules/storage-rules.txt` — answer images readable when signed in, writing restricted to
  admin; the rest of the bucket is closed.

Paste them into the Firebase Console.

## 🛠️ Tech Stack

- **Language**: Java (primary), Kotlin (utility extensions and ViewModel factories).
- **Min SDK**: 21 (Android 5.0) · **Compile SDK / Target SDK**: 34.
- **Build system**: Gradle 8.0 (wrapper), Android Gradle Plugin 7.3.0, Kotlin 1.9.22, Google Services plugin 4.3.13, Navigation Safe Args 2.5.3.
- **Requires JDK 11+** — Gradle 8.0 / AGP 7.3.0 refuse JDK 8.

### Key libraries

- **AndroidX**: AppCompat 1.5.1, Material 1.7.0, ConstraintLayout 2.1.4, Navigation 2.5.3, Lifecycle (ViewModel & LiveData) 2.5.1, RecyclerView 1.2.1, Preference 1.2.0, Core Splashscreen 1.0.0, Security Crypto 1.1.0-alpha06, ViewBinding.
- **Firebase**: Auth, Realtime Database, Storage, Cloud Messaging, FirebaseUI Storage 7.2.0, BOM 31.1.1.
- **Images & network**: Coil 2.5.0, Picasso 2.8, OkHttp 4.11.0 (signed proxy POSTs in `MinioProxyImageLoader`).
- **Other**: Gson 2.8.9, Kotlin Coroutines 1.6.1.
- **Tests**: JUnit 4.13.2, Espresso 3.5.0, androidx.test ext JUnit 1.1.4.

## 📁 Project Structure

```
├── README.md                  # this file
├── README-MINIO.md            # MinIO proxy: contract, server, cron, image migration
├── ARCHITECTURE_SUMMARY.md
├── firebase-rules/            # database-rules.json, storage-rules.txt
├── proxy/                     # fastapi_minio_proxy.py, cron_ping.py, upload_images.py, gen_vectors.py
├── app/
│   ├── build.gradle           # SDK levels, dependencies, ViewBinding
│   ├── google-services.json   # Firebase config for package com.dreamer.matholympappv1
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/java/com/dreamer/matholympappv1/
│       │   ├── MainActivity.java, MyApp.java
│       │   ├── data/          # model/model (Users, Zadachi, Razdel), repository/FirebaseAuthRepository
│       │   ├── domain/        # repository/AuthRepository, usecase/auth, usecase/session
│       │   ├── ui/ui/         # splash, login, register, mainscreen, razdel, scrollingscreen,
│       │   │                  # zadachascreen, settings (fragments, ViewModels, adapters)
│       │   └── utils/         # UserPrefsRepository, PrefKeys, MinioProxyImageLoader, FirebaseHelper,
│       │                      # InputValidator, NetworkManager, PermissionManager, MyArrayList,
│       │                      # StringIntegerConverter, action-bar helpers, ViewModelExtensions.kt
│       ├── main/res/          # layout, values, navigation, drawable, xml/root_preferences.xml
│       ├── test/             # unit tests, incl. utils/MinioProxyHmacTest
│       └── androidTest/      # instrumented tests
├── build.gradle, settings.gradle, versions.gradle
└── gradle/wrapper/            # Gradle 8.0 wrapper
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later, **JDK 11+**
- Firebase project with Authentication (Email/Password), Realtime Database, Storage and Cloud Messaging
  enabled, and `google-services.json` in the `app/` directory
- Realtime Database containing `Razdel/*` sections, otherwise the section list stays empty

### Installation

1. **Clone**
   ```bash
   git clone https://github.com/alextomskras/MathOlympAppv1
   cd MathOlympAppv1
   ```
2. **Configure Firebase** — create the project, add an Android app with package name
   `com.dreamer.matholympappv1`, download `google-services.json` into `app/`, paste the rules from
   `firebase-rules/` into the console.
3. **Open in Android Studio** — let Gradle sync complete, then build and run on an emulator or device.

### Build Commands

```bash
./gradlew assembleDebug        # debug build
./gradlew assembleRelease      # release build
./gradlew test                 # unit tests (incl. MinioProxyHmacTest)
./gradlew connectedCheck       # instrumented tests
```

### Optional: MinIO proxy deployment

Run `proxy/fastapi_minio_proxy.py` (Render), set `MINIO_PROXY_SECRET` equal to the secret entered in
the app Settings, install the cron ping (`proxy/cron_ping.py`), migrate existing images with
`proxy/upload_images.py`, then set URL + secret + enable switch in the app Settings screen. Settings
keys: `remote_proxy_base_url`, `remote_proxy_secret`, `remote_proxy_enabled` (default off; they live in
plain SharedPreferences and are cleared only on a full app reset).

## 🧪 Testing

- **Unit tests**: JUnit 4.13.2 — `utils/MinioProxyHmacTest` checks that the client HMAC signature
  matches the FastAPI proxy signature (vectors from `proxy/gen_vectors.py`).
- **UI tests**: Espresso 3.5.0, androidx.test ext JUnit 1.1.4.

```bash
./gradlew test            # unit tests
./gradlew connectedCheck  # instrumented tests
```

## 📌 Repository conventions

- Active work goes on feature branches (currently `improvements-p1-p6`), merged into `master` through a pull request.
- Superseded side branches are deleted after their tip is tagged `archive/<branch-name>`; archived history stays reachable through those tags.
- Commit messages follow Conventional Commits (`fix(...)`, `refactor(...)`, `feat(...)`) with a why-paragraph.

## 📄 License & Support

License terms are specified in the project repository. For support, open an issue in the repository.

---

**Package Name**: `com.dreamer.matholympappv1`
**Version**: 1.0 (versionCode 1)
**Minimum Android Version**: 5.0 (API Level 21)

---

Тестовая строка пуша: проверка, что git подхватывает учётные данные из менеджера паролей Windows и пуш проходит без интерактивного запроса токена.
