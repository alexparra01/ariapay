import SwiftUI

struct TransactionHistoryView: View {
    @State private var transactions: [TransactionModel] = [
        TransactionModel(id: "1", merchantName: "Coffee House", amount: 5.99, cardLastFour: "4242", isCompleted: true),
        TransactionModel(id: "2", merchantName: "Grocery Store", amount: 45.50, cardLastFour: "4242", isCompleted: true),
        TransactionModel(id: "3", merchantName: "Gas Station", amount: 35.00, cardLastFour: "5555", isCompleted: true),
        TransactionModel(id: "4", merchantName: "Restaurant", amount: 78.25, cardLastFour: "4242", isCompleted: true),
        TransactionModel(id: "5", merchantName: "Online Store", amount: 129.99, cardLastFour: "4242", isCompleted: false),
        TransactionModel(id: "6", merchantName: "Pharmacy", amount: 22.50, cardLastFour: "5555", isCompleted: true),
    ]
    
    var body: some View {
        List {
            ForEach(transactions) { transaction in
                TransactionRow(transaction: transaction)
                    .listRowSeparator(.hidden)
                    .listRowBackground(Color.clear)
            }
        }
        .listStyle(.plain)
        .navigationTitle("Transaction History")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button(action: refresh) {
                    Image(systemName: "arrow.clockwise")
                }
            }
        }
    }
    
    private func refresh() {
        // Refresh transactions
    }
}

#Preview {
    NavigationStack {
        TransactionHistoryView()
    }
}
