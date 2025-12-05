import SwiftUI
import shared

@main
struct AriaPayApp: App {
    
    init() {
        // Initialize Koin
        KoinInitializer.shared.initialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

// Koin Initializer
class KoinInitializer {
    static let shared = KoinInitializer()
    
    func initialize() {
        // Start Koin with shared modules
        // This will be called from Swift to initialize the DI container
        #if DEBUG
        print("Initializing Koin...")
        #endif
    }
}
