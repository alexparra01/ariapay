import SwiftUI

struct LoginView: View {
    @State private var email = "demo@ariapay.com"
    @State private var password = "password123"
    @State private var isLoading = false
    @State private var errorMessage: String?
    
    let onLoginSuccess: () -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                Spacer().frame(height: 60)
                
                // Logo
                Text("💳")
                    .font(.system(size: 64))
                
                Text("AriaPay")
                    .font(.system(size: 32, weight: .bold))
                    .foregroundColor(AriaPayColors.primary)
                
                Text("Welcome back")
                    .font(.system(size: 16))
                    .foregroundColor(.secondary)
                
                Spacer().frame(height: 24)
                
                // Email field
                VStack(alignment: .leading, spacing: 8) {
                    Text("Email")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    TextField("Email", text: $email)
                        .textFieldStyle(.roundedBorder)
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                }
                
                // Password field
                VStack(alignment: .leading, spacing: 8) {
                    Text("Password")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    SecureField("Password", text: $password)
                        .textFieldStyle(.roundedBorder)
                }
                
                // Error message
                if let error = errorMessage {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(AriaPayColors.error)
                }
                
                Spacer().frame(height: 16)
                
                // Login button
                Button(action: login) {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    } else {
                        Text("Sign In")
                            .font(.headline)
                    }
                }
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(AriaPayColors.primary)
                .foregroundColor(.white)
                .cornerRadius(12)
                .disabled(isLoading || email.isEmpty || password.isEmpty)
                
                // Demo hint
                Text("Demo: Use pre-filled credentials")
                    .font(.caption)
                    .foregroundColor(AriaPayColors.secondary)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(AriaPayColors.secondary.opacity(0.1))
                    .cornerRadius(8)
                
                Spacer()
            }
            .padding(24)
        }
    }
    
    private func login() {
        isLoading = true
        errorMessage = nil
        
        // Simulate login delay
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            if email == "demo@ariapay.com" && password == "password123" {
                onLoginSuccess()
            } else {
                errorMessage = "Invalid email or password"
            }
            isLoading = false
        }
    }
}

#Preview {
    LoginView(onLoginSuccess: {})
}
