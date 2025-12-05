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
| **DI** | Koin | - | Koin |
| **Networking** | - | - | Ktor |
| **Serialization** | - | - | Kotlinx Serialization |
| **NFC** | HCE Service | CoreNFC | - |

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog+
- Xcode 15+
- JDK 17

### Build Android
```bash
./gradlew :androidApp:assembleDebug
```

### Build iOS
1. Generate the shared framework:
```bash
./gradlew :shared:linkDebugFrameworkIosArm64
```
2. Open `iosApp/iosApp.xcodeproj` in Xcode
3. Build and run

## 📱 Demo Credentials
- **Email**: demo@ariapay.com
- **Password**: password123

## 🏗️ Architecture

### Shared Module
Contains all business logic that's shared between platforms:
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
- iOS 15+ design patterns
- SwiftUI Navigation
- ObservableObject ViewModels
- CoreNFC ready (requires entitlements)

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

Then update the DI module:
```kotlin
val apiModule = module {
    single<AriaPayApi> { RealAriaPayApi(get()) }
}
```

## ⚠️ Production Notes

1. **Security**: Implement proper encryption and tokenization
2. **NFC AIDs**: Register with payment networks (Visa, Mastercard)
3. **iOS Payments**: Integrate Apple Pay via PassKit
4. **PCI Compliance**: Follow PCI-DSS requirements

## 📄 License

This is a blueprint/template project. Modify and use as needed.
