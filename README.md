# FF Voice Changer - Android Real-Time Voice Processing App

A modern, high-performance Android application built with Kotlin and Jetpack Compose featuring real-time audio processing, custom voice transformation presets, pitch shifting, audio visualization, background foreground services, and WAV audio recording/export.

---

## 📱 Project Overview & Structure

This project follows the standard Android Studio layout and modern Jetpack Compose architecture:

```
├── .github/
│   └── workflows/
│       └── build-apk.yml              # GitHub Actions CI workflow for automatic APK build & release
├── app/
│   ├── build.gradle.kts               # App module build configuration & dependencies
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml    # App permissions, components & service definitions
│       │   ├── java/com/example/      # Application Kotlin source code
│       │   │   ├── MainActivity.kt    # Entry Activity with edge-to-edge & theme setup
│       │   │   ├── audio/             # Low-latency AudioRecord/AudioTrack engine & DSP
│       │   │   ├── data/              # Room Database, DAOs & Entities
│       │   │   ├── service/           # VoiceChangerService (Foreground Service)
│       │   │   └── ui/                # Jetpack Compose UI (Screens, Theme, Components)
│       │   └── res/                   # Vector drawables, strings, colors, launcher icons
│       └── test/                      # Unit & Robolectric test suites
├── gradle/
│   └── libs.versions.toml             # Gradle Version Catalog for dependency versions
├── build.gradle.kts                   # Root build script
├── settings.gradle.kts                 # Project settings & module definition
└── README.md                          # Project documentation
```

---

## ⚙️ How to Configure App Name and Application ID

You can easily customize the **App Name** and **Package / Application ID** before building:

### 1. Change the Application ID (Package Name)
Open `app/build.gradle.kts` and modify the `defaultConfig` section:

```kotlin
android {
    namespace = "com.example" // Internal R class namespace

    defaultConfig {
        applicationId = "YOUR.CUSTOM.PACKAGE.NAME" // e.g., com.mycompany.voicechanger
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}
```

### 2. Change the App Name
Open `app/src/main/res/values/strings.xml` and edit the `app_name` string resource:

```xml
<resources>
    <string name="app_name">Your Custom App Name</string>
</resources>
```

---

## 🛠️ How to Build into a Real Installable APK

### Option A: Using Command Line (Gradle)

1. **Build Debug APK**:
   Run the following command from the project root directory:
   ```bash
   gradle :app:assembleDebug --stacktrace
   ```
   The generated APK will be available at:
   `app/build/outputs/apk/debug/app-debug.apk`

2. **Build Release APK**:
   Set your keystore path and passwords via environment variables (or configure signing in `app/build.gradle.kts`), then run:
   ```bash
   gradle :app:assembleRelease --stacktrace
   ```
   The generated APK will be available at:
   `app/build/outputs/apk/release/app-release.apk`

---

### Option B: Using Android Studio

1. Open Android Studio and select **Open an Existing Project**.
2. Select the root directory of this project.
3. Wait for Gradle sync to complete.
4. From the top menu bar, select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Once complete, click **locate** in the popup notification to find your `.apk` file.

---

## 🚀 How to Upload the APK to GitHub via GitHub Actions

This repository includes an automated GitHub Actions workflow (`.github/workflows/build-apk.yml`).

### How It Works:
1. Push your code to your GitHub repository on the `main` branch:
   ```bash
   git add .
   git commit -m "Configure release build workflow"
   git push origin main
   ```
2. GitHub Actions will automatically trigger, setup Java 17 and Gradle 9.3.1, compile the Android project, and generate `app-debug.apk`.
3. The workflow will automatically publish a new **GitHub Release** with the compiled `.apk` file directly downloadable under the **Assets** section.
4. The APK is also preserved as a build artifact named `app-debug-apk` on the Actions run summary page.

---

## 🔒 Permissions & Privacy

This application operates **100% on-device** without transmitting any microphone audio to external servers.

Required Manifest Permissions:
- `RECORD_AUDIO`: Required for live audio processing and recording.
- `POST_NOTIFICATIONS`: Required for Android 13+ background foreground service controls.
- `FOREGROUND_SERVICE`: Ensures continuous audio processing when minimized.
