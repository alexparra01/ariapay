# AriaPay - KMP NFC Payments App

A Kotlin Multiplatform (KMP) NFC payments application with:
- **Shared business logic** (Kotlin) for Android & iOS
- **Android UI**: Pure Jetpack Compose
- **iOS UI**: Pure SwiftUI

## 📁 Project Structure

```
ariapay/
├── shared/                    # KMP Shared Module
│   └── src/
│       ├── commonMain/        # Shared business logic
│       │   ├── data/
│       │   │   ├── api/       # API interfaces
│       │   │   ├── mock/      # Mock API implementation
│       │   │   └── repository/
│       │   ├── domain/
│       │   │   ├── model/     # Data models
│       │   │   └── usecase/   # Use cases
│       │   └── di/            # Koin DI modules
│       ├── androidMain/       # Android-specific code
│       └── iosMain/           # iOS-specific code
│
├── androidApp/                # Android App (Jetpack Compose)
│   └── src/main/
│       └── kotlin/com/ariapay/
│           ├── ui/
│           │   ├── screens/   # Compose screens
│           │   ├── components/# Compose components
│           │   ├── theme/     # Material3 theme
│           │   └── navigation/
│           ├── viewmodel/     # Android ViewModels
│           └── nfc/           # HCE Service
│
└── iosApp/                    # iOS App (SwiftUI)
    └── iosApp/
        ├── Views/             # SwiftUI views
        ├── ViewModels/        # iOS ViewModels
        └── Services/          # iOS services
```

## 🔧 Tech Stack

| Layer | Android | iOS | Shared |
|-------|---------|-----|--------|
| **UI** | Jetpack Compose | SwiftUI | - |
| **Architecture** | MVVM | MVVM | Clean Architecture |
| **DI** | Koin | Koin | Koin |
| **Serialization** | - | - | Kotlinx Serialization |
| **NFC** | HCE Service | CoreNFC | - |

## 📋 Prerequisites

- **Java 17** (required)
- **Android Studio Hedgehog+**
- **Xcode 16.x** (Xcode 26+ not yet supported by Kotlin)
- **Gradle 8.7+**

### Install Java 17 (if needed)
```bash
brew install openjdk@17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17' >> ~/.zshrc
source ~/.zshrc
```

### Install Gradle (if needed)
```bash
brew install gradle@8
brew link gradle@8 --force
```

## 🚀 Getting Started

### 1. Generate Gradle Wrapper
```bash
cd ariapay
gradle wrapper --gradle-version 8.7
chmod +x gradlew
```

### 2. Build Android
```bash
./gradlew :androidApp:assembleDebug
```

### 3. Build iOS Shared Framework
```bash
./gradlew :shared:assembleSharedDebugXCFramework
```

Verify the framework was created:
```bash
ls -la shared/build/XCFrameworks/debug/
```

### 4. Setup Xcode Project

1. **Open the Xcode project:**
   ```bash
   open iosApp/iosApp.xcodeproj
   ```

2. **Add the shared framework:**
    - Select your project in the navigator
    - Select the **iosApp** target
    - Go to **General** tab
    - Scroll to **Frameworks, Libraries, and Embedded Content**
    - Click **+** → **Add Other...** → **Add Files...**
    - Navigate to `shared/build/XCFrameworks/debug/shared.xcframework`
    - Set to **Embed & Sign**

3. **Add Framework Search Path:**
    - Go to **Build Settings** tab
    - Search for "Framework Search Paths"
    - Add: `$(SRCROOT)/../shared/build/XCFrameworks/debug`

4. **Clean and Build:**
    - Product → Clean Build Folder (Cmd+Shift+K)
    - Build (Cmd+B)

## 📱 Demo Credentials

- **Email**: demo@ariapay.com
- **Password**: password123

## 🏗️ Architecture

### Shared Module
Contains all business logic shared between platforms:
- **Models**: Transaction, User, PaymentCard, Wallet, etc.
- **Repository**: PaymentRepository with mock implementation
- **Use Cases**: LoginUseCase, QuickPayUseCase, etc.
- **DI**: Koin modules for dependency injection

### Android App
Native Android UI with Jetpack Compose:
- Material3 design system
- Compose Navigation
- AndroidX ViewModel
- Koin for DI
- HCE (Host Card Emulation) for NFC payments

### iOS App
Native iOS UI with SwiftUI:
- iOS 17+ design patterns
- SwiftUI Navigation
- ObservableObject ViewModels
- Shared Kotlin framework via KoinHelper

## 🔐 NFC Implementation

### Android
Uses Host Card Emulation (HCE) to emulate contactless cards:
```kotlin
class AriaPayHceService : HostApduService() {
    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        // Handle APDU commands
    }
}
```

### iOS
iOS doesn't support HCE. For real payments, integrate with Apple Pay via PassKit.
NFC reading is available via CoreNFC for reading terminal data.

## 📝 Switching to Real Backend

Replace `MockAriaPayApi` with your real API implementation:

```kotlin
class RealAriaPayApi(private val httpClient: HttpClient) : AriaPayApi {
    override suspend fun createTransaction(request: TransactionRequest): TransactionResponse {
        return httpClient.post("https://your-api.com/transactions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
```

Then update the DI module in `shared/src/commonMain/kotlin/com/ariapay/di/SharedModule.kt`:
```kotlin
val apiModule = module {
    single<AriaPayApi> { RealAriaPayApi(get()) }
}
```

## 🔄 Rebuilding After Changes

### After modifying shared module:
```bash
./gradlew :shared:assembleSharedDebugXCFramework
```
Then in Xcode: Product → Clean Build Folder → Build

### After modifying Android app:
```bash
./gradlew :androidApp:assembleDebug
```

## ⚠️ Troubleshooting

### "No such module 'shared'" in Xcode
1. Ensure framework is built: `./gradlew :shared:assembleSharedDebugXCFramework`
2. Check framework exists: `ls shared/build/XCFrameworks/debug/`
3. Re-add framework to Xcode (see Setup step 4)
4. Clean build folder and rebuild

### "Xcode 26+ not supported"
Kotlin doesn't support Xcode 26 beta yet. Downgrade to Xcode 16.x:
```bash
sudo xcode-select -s /Applications/Xcode-16.4.app
```

### Java version errors
Ensure Java 17 is set:
```bash
java -version  # Should show 17.x
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
```

### Gradle wrapper missing
```bash
gradle wrapper --gradle-version 8.7
chmod +x gradlew
```

## ⚠️ Production Notes

1. **Security**: Implement proper encryption and tokenization
2. **NFC AIDs**: Register with payment networks (Visa, Mastercard)
3. **iOS Payments**: Integrate Apple Pay via PassKit
4. **PCI Compliance**: Follow PCI-DSS requirements

## 📄 License

This is a blueprint/template project. Modify and use as needed.