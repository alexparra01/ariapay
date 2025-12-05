import SwiftUI

struct ContentView: View {
    @StateObject private var appState = AppState()
    
    var body: some View {
        NavigationStack {
            switch appState.currentScreen {
            case .splash:
                SplashView()
                    .onAppear {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                            withAnimation {
                                appState.currentScreen = appState.isLoggedIn ? .home : .login
                            }
                        }
                    }
            case .login:
                LoginView(onLoginSuccess: {
                    withAnimation {
                        appState.isLoggedIn = true
                        appState.currentScreen = .home
                    }
                })
            case .home:
                HomeView(appState: appState)
            }
        }
        .environmentObject(appState)
    }
}

class AppState: ObservableObject {
    @Published var currentScreen: Screen = .splash
    @Published var isLoggedIn: Bool = false
    
    enum Screen {
        case splash, login, home
    }
    
    func logout() {
        isLoggedIn = false
        currentScreen = .login
    }
}

#Preview {
    ContentView()
}
