<p align="center">
    <img src="https://github.com/ClementVicart/PixelCount/blob/main/kmp/assets/icon-192.png?raw=true" />
</p>

<p align="center">
    <a href="https://play.google.com/store/apps/details?id=dev.vicart.pixelcount">
        <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/play-store-badge.png?raw=true" />
    </a>
</p>

# PixelCount - Expense tracking and splitting

PixelCount is a **Kotlin Multiplatform** application designed to help you track and split expenses between groups of friends or colleagues. Whether you're on a ski vacation, sharing a dinner, or collaborating on a project, PixelCount makes it easy to manage who owes what and settle accounts fairly.

## ✨ Features

- 💰 **Easy Expense Tracking** - Add expenses and track who paid for what
- 👥 **Group Management** - Create groups with friends or colleagues
- 🧮 **Automatic Settlement** - Calculate who owes whom and how much
- 📱 **Cross-Platform** - Use the same app on your phone, desktop, or smartwatch
- ⌚ **WearOS Support** - Access your expenses on your smartwatch
- 💾 **Local Storage** - Your data is stored locally on your device
- 🔄 **Real-time Sync** - Seamless synchronization between devices via Google Play Services
- 📊 **Expense History** - View detailed transaction history

## 📷 Screenshots

<p align="center">
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221927.png?raw=true" width="150"/>
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221936.png?raw=true" width="150"/>
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221942.png?raw=true" width="150"/>
  <img src="https://github.com/ClementVicart/PixelCount/blob/main/docs/Screenshot_20260123_221947.png?raw=true" width="150"/>
</p>

## 🚀 Platforms

PixelCount is built using **Kotlin Multiplatform** and **Compose Multiplatform**, currently supporting:

- 📱 **Android** (Phone & Tablet)
- ⌚ **WearOS** (Smartwatch)
- 🖥️ **Desktop** (Windows, Linux)

## 🛠 Technology Stack

### Core Technologies
- **Language**: Kotlin 2.3.0
- **Multiplatform**: Kotlin Multiplatform (KMP)
- **UI Framework**: Compose Multiplatform 1.10.0

### UI Components
- **Compose Material 3**: 1.10.0-alpha05
- **Material Icons**: 1.7.3
- **Navigation**: Compose Navigation3 1.0.0-alpha06
- **Adaptive Layout**: Adaptive Navigation 1.3.0-alpha02
- **WearOS Compose**: 1.5.6

### Data & Persistence
- **SQLDelight**: 2.2.1 (Multiplatform SQL database)
- **Serialization**: kotlinx-serialization 1.9.0

### Async & Concurrency
- **Coroutines**: kotlinx-coroutines 1.10.2
- **DateTime**: kotlinx-datetime 0.7.1

### Android Specific
- **Android Gradle Plugin**: 9.0.0
- **Min SDK**: 30
- **Target SDK**: 36
- **Activity Compose**: 1.12.2
- **Material Design**: 1.13.0

### Device Integration
- **Google Play Services Wearable**: 19.0.0 (For Android/WearOS sync)

### Build Tools
- **Gradle**: Kotlin DSL

## 🏗️ Project Structure

```
PixelCount/
├── androidApp/          # Android phone application
├── wearosApp/          # WearOS smartwatch application
├── kmp/                # Kotlin Multiplatform shared code + Desktop
├── shared/             # Shared business logic and data models
├── gradle/             # Gradle configuration
└── README.md           # This file
```

## Getting Started

### Prerequisites
- Java JDK 11 or higher
- Kotlin 2.3.0
- Gradle 8.0 or higher (or use the included Gradle wrapper)
- Android SDK (for Android builds)

### Building the Project

#### Android
```bash
./gradlew androidApp:build
```

#### Desktop
```bash
./gradlew kmp:build
```

#### WearOS
```bash
./gradlew wearosApp:build
```

#### Run Desktop App
```bash
./gradlew kmp:run
```

---

**Made with ❤️ using Kotlin Multiplatform**
