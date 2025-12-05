import SwiftUI

struct AriaPayColors {
    static let primary = Color(hex: "1A237E")
    static let primaryLight = Color(hex: "534BAE")
    static let secondary = Color(hex: "00BFA5")
    static let secondaryLight = Color(hex: "5DF2D6")
    static let success = Color(hex: "4CAF50")
    static let error = Color(hex: "E53935")
    static let warning = Color(hex: "FF9800")
    static let background = Color(hex: "F5F5F5")
    static let surface = Color.white
    static let cardGradient = LinearGradient(
        colors: [primary, primaryLight],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )
}

extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default: (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(.sRGB, red: Double(r) / 255, green: Double(g) / 255, blue: Double(b) / 255, opacity: Double(a) / 255)
    }
}
