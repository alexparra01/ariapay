import SwiftUI

struct SettingsView: View {
    @ObservedObject var appState: AppState
    
    var body: some View {
        List {
            Section {
                SettingsRow(icon: "👤", title: "Profile", subtitle: "Manage your account")
                SettingsRow(icon: "🔔", title: "Notifications", subtitle: "Manage notifications")
                SettingsRow(icon: "🔒", title: "Security", subtitle: "Password, biometrics")
                SettingsRow(icon: "❓", title: "Help & Support", subtitle: "FAQ, contact support")
            }
            
            Section {
                Button(action: { appState.logout() }) {
                    HStack {
                        Text("Sign Out")
                            .foregroundColor(.white)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(AriaPayColors.error)
                    .cornerRadius(12)
                }
                .listRowBackground(Color.clear)
            }
            
            Section {
                Text("AriaPay v1.0.0")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity)
            }
            .listRowBackground(Color.clear)
        }
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct SettingsRow: View {
    let icon: String
    let title: String
    let subtitle: String
    
    var body: some View {
        HStack(spacing: 16) {
            Text(icon)
                .font(.title2)
            
            VStack(alignment: .leading) {
                Text(title)
                    .font(.subheadline.weight(.medium))
                Text(subtitle)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            Image(systemName: "chevron.right")
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 8)
    }
}

#Preview {
    NavigationStack {
        SettingsView(appState: AppState())
    }
}
