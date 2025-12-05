import SwiftUI

struct PaymentView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var amount = "25.99"
    @State private var isProcessing = false
    @State private var showResult = false
    @State private var paymentSuccess = false
    @State private var resultMessage = ""
    
    var body: some View {
        VStack(spacing: 24) {
            if isProcessing {
                Spacer()
                NfcPulseAnimation()
                Text("Processing payment...")
                    .font(.title3.weight(.medium))
                Spacer()
            } else if showResult {
                PaymentResultView(
                    success: paymentSuccess,
                    message: resultMessage,
                    onDismiss: { dismiss() }
                )
            } else {
                // Card preview
                PaymentCardView(card: PaymentCardModel(
                    lastFour: "4242",
                    holderName: "JOHN DOE",
                    expiry: "12/27",
                    type: "VISA",
                    isDefault: true
                ))
                .padding(.horizontal)
                
                // Amount input
                VStack(spacing: 16) {
                    Text("Demo Payment")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    
                    TextField("Amount (USD)", text: $amount)
                        .textFieldStyle(.roundedBorder)
                        .keyboardType(.decimalPad)
                    
                    Text("Merchant: Demo Store")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding()
                .background(Color.white)
                .cornerRadius(16)
                .shadow(color: .black.opacity(0.05), radius: 4)
                .padding(.horizontal)
                
                Spacer()
                
                Text("Hold your phone near the terminal\nor tap the button below")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                
                Button(action: processPayment) {
                    HStack {
                        Text("📱")
                        Text("Simulate NFC Payment")
                            .font(.headline)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 64)
                    .background(AriaPayColors.primary)
                    .foregroundColor(.white)
                    .cornerRadius(16)
                }
                .padding(.horizontal)
            }
        }
        .padding(.vertical)
        .navigationTitle("NFC Payment")
        .navigationBarTitleDisplayMode(.inline)
    }
    
    private func processPayment() {
        guard let amountValue = Double(amount), amountValue > 0 else { return }
        
        isProcessing = true
        
        // Simulate payment processing
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            isProcessing = false
            paymentSuccess = Bool.random() || true // 90% success rate simulation
            resultMessage = paymentSuccess ? "Transaction ID: txn_\(Int.random(in: 1000...9999))" : "Payment declined"
            showResult = true
        }
    }
}

struct NfcPulseAnimation: View {
    @State private var scale: CGFloat = 0.5
    @State private var opacity: Double = 0.6
    
    var body: some View {
        ZStack {
            ForEach(0..<3) { index in
                Circle()
                    .fill(AriaPayColors.secondary.opacity(opacity))
                    .frame(width: 120, height: 120)
                    .scaleEffect(scale)
                    .animation(
                        Animation.easeOut(duration: 1)
                            .repeatForever(autoreverses: false)
                            .delay(Double(index) * 0.33),
                        value: scale
                    )
            }
            
            Circle()
                .fill(AriaPayColors.primary)
                .frame(width: 80, height: 80)
                .overlay(
                    Text("📱")
                        .font(.system(size: 32))
                )
        }
        .onAppear {
            scale = 1.5
            opacity = 0
        }
    }
}

struct PaymentResultView: View {
    let success: Bool
    let message: String
    let onDismiss: () -> Void
    
    @State private var scale: CGFloat = 0
    
    var body: some View {
        VStack(spacing: 32) {
            Spacer()
            
            Circle()
                .fill(success ? AriaPayColors.success : AriaPayColors.error)
                .frame(width: 120, height: 120)
                .overlay(
                    Text(success ? "✓" : "✗")
                        .font(.system(size: 64))
                        .foregroundColor(.white)
                )
                .scaleEffect(scale)
            
            Text(success ? "Payment Successful!" : "Payment Failed")
                .font(.title.bold())
                .foregroundColor(success ? AriaPayColors.success : AriaPayColors.error)
            
            Text(message)
                .font(.subheadline)
                .foregroundColor(.secondary)
            
            Spacer()
            
            Button(action: onDismiss) {
                Text("Back to Home")
                    .font(.headline)
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(AriaPayColors.primary)
                    .foregroundColor(.white)
                    .cornerRadius(12)
            }
            .padding(.horizontal)
        }
        .onAppear {
            withAnimation(.spring(response: 0.5, dampingFraction: 0.6)) {
                scale = 1
            }
        }
    }
}

#Preview {
    NavigationStack {
        PaymentView()
    }
}
