# MathOlympAppv1

An Android application for mathematics olympiad preparation and practice, built with Firebase backend integration.

## 📱 Features

- **User Authentication**: Email/password login and registration powered by Firebase Authentication
- **Problem Database**: Access to mathematics problems organized by categories
- **Real-time Updates**: Firebase Cloud Messaging for notifications
- **Modern UI**: Material Design components with ViewBinding
- **Offline Support**: Firebase Realtime Database integration
- **Image Handling**: Firebase Storage for image uploads/downloads using Picasso and Glide

## 🏗️ Architecture

The app follows a structured architecture with the following main components:

### Data Layer
- **Models**: `Users`, `Zadachi` (Problems), `Razdel` (Sections/Categories)
- **Placeholder Content**: For list management and UI display

### UI Layer
- **Login Screen**: User authentication interface
- **Register Screen**: New user registration
- **Main Screen**: Primary navigation and content display
- **Settings**: App configuration options
- **Scrolling Screen**: Content browsing interface

### Utilities
- Firebase helpers and authentication utilities
- Shared preferences management
- Network and permission managers
- Custom data structures (`MyArrayList`, `StringIntegerConverter`)

## 🛠️ Tech Stack

- **Language**: Java (primary), Kotlin (utilities)
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 32 (Android 12L)
- **Build System**: Gradle 7.3.0

### Key Libraries & Dependencies

#### AndroidX
- AppCompat 1.5.1
- Material Design 1.7.0
- ConstraintLayout 2.1.4
- Navigation Component 2.5.3
- Lifecycle (ViewModel & LiveData) 2.5.1
- RecyclerView 1.2.1
- ViewBinding

#### Firebase
- Firebase Authentication
- Firebase Realtime Database
- Firebase Storage
- Firebase Cloud Messaging
- Firebase BOM 31.1.1

#### Image Loading
- Picasso 2.8
- Glide 4.14.2
- FirebaseUI Storage 7.2.0

#### Other
- Gson 2.8.9
- Kotlin Coroutines 1.6.1

## 📁 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/dreamer/matholympappv1/
│   │   │   ├── data/
│   │   │   │   ├── model/model/       # Data models (Users, Zadachi, Razdel)
│   │   │   │   └── placeholder/        # Placeholder content for lists
│   │   │   ├── ui/ui/                  # UI components (Fragments, ViewModels)
│   │   │   │   ├── login/              # Login functionality
│   │   │   │   ├── register/           # Registration functionality
│   │   │   │   ├── mainscreen/         # Main app screen
│   │   │   │   ├── settings/           # Settings screen
│   │   │   │   └── razdel/             # Category/section screens
│   │   │   ├── utils/                  # Utility classes
│   │   │   │   ├── FirebaseHelper.java
│   │   │   │   ├── SharedPreffUtils.java
│   │   │   │   ├── NetworkManager.java
│   │   │   │   └── PermissionManager.java
│   │   │   └── firebase/               # Firebase services
│   │   ├── res/                        # Android resources
│   │   │   ├── layout/                 # XML layouts
│   │   │   ├── values/                 # Strings, colors, styles
│   │   │   ├── navigation/             # Navigation graphs
│   │   │   └── drawable/               # Images and icons
│   │   └── AndroidManifest.xml
│   ├── test/                           # Unit tests
│   └── androidTest/                    # Instrumented tests
├── build.gradle                        # App-level build configuration
└── proguard-rules.pro                  # ProGuard rules
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 8 or higher
- Firebase project with:
  - Authentication enabled (Email/Password)
  - Realtime Database configured
  - Storage bucket set up
  - Cloud Messaging enabled
  - `google-services.json` placed in `app/` directory

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd MathOlympAppv1
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name `com.dreamer.matholympappv1`
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Authentication, Database, Storage, and Cloud Messaging in Firebase Console

3. **Open in Android Studio**
   - Open the project in Android Studio
   - Let Gradle sync complete
   - Build and run on an emulator or physical device

### Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

## ⚙️ Configuration

### Firebase Setup

Ensure the following Firebase services are properly configured:

1. **Authentication**: Enable Email/Password sign-in method
2. **Realtime Database**: Set up security rules for data access
3. **Storage**: Configure storage rules for file uploads
4. **Cloud Messaging**: Set up notification channels

### Build Variants

- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard optimization (currently disabled)

## 🧪 Testing

The project includes test configurations for:
- **Unit Tests**: JUnit 4.13.2
- **UI Tests**: Espresso 3.5.0, AndroidX Test Ext JUnit 1.1.4

Run tests using:
```bash
./gradlew test          # Unit tests
./gradlew connectedCheck  # Instrumented tests
```

## 📄 License

This project is licensed under the terms specified in the project repository.

## 👥 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## 📞 Support

For support, please open an issue in the repository or contact the development team.

---

**Package Name**: `com.dreamer.matholympappv1`  
**Version**: 1.0  
**Minimum Android Version**: 5.0 (API Level 21)
