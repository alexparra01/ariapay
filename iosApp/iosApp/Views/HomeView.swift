import SwiftUI

struct HomeView: View {
    @ObservedObject var appState: AppState
    @StateObject private var viewModel = HomeViewModel()
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 24) {
                    // Header
                    HStack {
                        VStack(alignment: .leading) {
                            Text("AriaPay")
                                .font(.title.bold())
                                .foregroundColor(AriaPayColors.primary)
                            Text("Ready to pay")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        NavigationLink(destination: SettingsView(appState: appState)) {
                            Text("⚙️")
                                .font(.title2)
                        }
                    }
                    
                    // Card
                    PaymentCardView(card: viewModel.defaultCard)
                    
                    // Pay button
                    NavigationLink(destination: PaymentView()) {
                        HStack {
                            Text("📱")
                            Text("Tap to Pay")
                                .font(.headline)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 64)
                        .background(AriaPayColors.secondary)
                        .foregroundColor(.black)
                        .cornerRadius(16)
                    }
                    
                    // Recent transactions
                    HStack {
                        Text("Recent Transactions")
                            .font(.headline)
                        Spacer()
                        NavigationLink(destination: TransactionHistoryView()) {
                            Text("See All")
                                .font(.subheadline)
                                .foregroundColor(AriaPayColors.primary)
                        }
                    }
                    
                    if viewModel.recentTransactions.isEmpty {
                        Text("No transactions yet.")
                            .foregroundColor(.secondary)
                            .padding(.vertical, 32)
                    } else {
                        ForEach(viewModel.recentTransactions.prefix(5)) { transaction in
                            TransactionRow(transaction: transaction)
                        }
                    }
                }
                .padding(16)
            }
            .navigationBarHidden(true)
        }
    }
}

struct PaymentCardView: View {
    let card: PaymentCardModel
    
    var body: some View {
        ZStack {
            AriaPayColors.cardGradient
            
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    Text("AriaPay")
                        .font(.title3.bold())
                        .foregroundColor(.white)
                    Spacer()
                    if card.isDefault {
                        Text("DEFAULT")
                            .font(.caption2.bold())
                            .foregroundColor(.black)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 4)
                            .background(AriaPayColors.secondary)
                            .cornerRadius(12)
                    }
                }
                
                Spacer()
                
                Text("•••• •••• •••• \(card.lastFour)")
                    .font(.title2)
                    .foregroundColor(.white.opacity(0.9))
                    .tracking(2)
                
                Spacer()
                
                HStack {
                    VStack(alignment: .leading) {
                        Text("CARD HOLDER")
                            .font(.caption2)
                            .foregroundColor(.white.opacity(0.6))
                        Text(card.holderName)
                            .font(.subheadline)
                            .foregroundColor(.white)
                    }
                    Spacer()
                    VStack(alignment: .trailing) {
                        Text("EXPIRES")
                            .font(.caption2)
                            .foregroundColor(.white.opacity(0.6))
                        Text(card.expiry)
                            .font(.subheadline)
                            .foregroundColor(.white)
                    }
                    Spacer()
                    Text(card.type)
                        .font(.headline)
                        .foregroundColor(.white)
                }
            }
            .padding(24)
        }
        .frame(height: 200)
        .cornerRadius(16)
        .shadow(radius: 8)
    }
}

struct TransactionRow: View {
    let transaction: TransactionModel
    
    var body: some View {
        HStack(spacing: 12) {
            Circle()
                .fill(transaction.isCompleted ? AriaPayColors.success.opacity(0.1) : AriaPayColors.error.opacity(0.1))
                .frame(width: 48, height: 48)
                .overlay(
                    Text(transaction.isCompleted ? "✓" : "✗")
                        .foregroundColor(transaction.isCompleted ? AriaPayColors.success : AriaPayColors.error)
                )
            
            VStack(alignment: .leading) {
                Text(transaction.merchantName)
                    .font(.subheadline.weight(.medium))
                Text("•••• \(transaction.cardLastFour)")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Text("$\(String(format: "%.2f", transaction.amount))")
                .font(.subheadline.bold())
        }
        .padding(16)
        .background(Color.white)
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.05), radius: 2)
    }
}

// View Models and Models
class HomeViewModel: ObservableObject {
    @Published var defaultCard = PaymentCardModel(
        lastFour: "4242",
        holderName: "JOHN DOE",
        expiry: "12/27",
        type: "VISA",
        isDefault: true
    )
    
    @Published var recentTransactions: [TransactionModel] = [
        TransactionModel(id: "1", merchantName: "Coffee House", amount: 5.99, cardLastFour: "4242", isCompleted: true),
        TransactionModel(id: "2", merchantName: "Grocery Store", amount: 45.50, cardLastFour: "4242", isCompleted: true),
        TransactionModel(id: "3", merchantName: "Gas Station", amount: 35.00, cardLastFour: "5555", isCompleted: true),
    ]
}

struct PaymentCardModel {
    let lastFour: String
    let holderName: String
    let expiry: String
    let type: String
    let isDefault: Bool
}

struct TransactionModel: Identifiable {
    let id: String
    let merchantName: String
    let amount: Double
    let cardLastFour: String
    let isCompleted: Bool
}

#Preview {
    HomeView(appState: AppState())
}
