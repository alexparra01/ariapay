import SwiftUI

struct SplashView: View {
    @State private var scale: CGFloat = 0.5
    @State private var opacity: Double = 0
    
    var body: some View {
        ZStack {
            AriaPayColors.primary.ignoresSafeArea()
            
            VStack(spacing: 24) {
                Text("💳")
                    .font(.system(size: 80))
                
                Text("AriaPay")
                    .font(.system(size: 36, weight: .bold))
                    .foregroundColor(.white)
                
                Text("Tap. Pay. Go.")
                    .font(.system(size: 18))
                    .foregroundColor(.white.opacity(0.8))
            }
            .scaleEffect(scale)
            .opacity(opacity)
        }
        .onAppear {
            withAnimation(.spring(response: 0.6, dampingFraction: 0.6)) {
                scale = 1.0
                opacity = 1.0
            }
        }
    }
}

#Preview {
    SplashView()
}
