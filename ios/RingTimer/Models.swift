import SwiftUI

// MARK: - Colors

extension Color {
    init(hex: UInt32) {
        self.init(
            red: Double((hex >> 16) & 0xFF) / 255,
            green: Double((hex >> 8) & 0xFF) / 255,
            blue: Double(hex & 0xFF) / 255
        )
    }
}

enum Theme {
    static let ink = Color(hex: 0x0B0A0F)
    static let panel = Color(hex: 0x16141D)
    static let line = Color(hex: 0x2A2733)
    static let text = Color(hex: 0xF4F1FA)
    static let muted = Color(hex: 0x8D88A0)
    static let tabBar = Color(hex: 0x09080C)
}

// MARK: - Modes

enum Mode: String, CaseIterable, Identifiable {
    case warmup, training, rest, stretch

    var id: String { rawValue }

    var color: Color {
        switch self {
        case .warmup: return Color(hex: 0xFF8C1A)   // orange-yellow
        case .training: return Color(hex: 0xE8202F) // red
        case .rest: return Color(hex: 0xFFD21F)     // yellow
        case .stretch: return Color(hex: 0x2F8BFF)  // blue
        }
    }

    /// The three quick presets (the 4th button is "Custom").
    var presets: [Int] {
        switch self {
        case .warmup, .stretch: return [120, 180, 300]
        case .training: return [60, 120, 180]
        case .rest: return [30, 45, 60]
        }
    }

    var defaultDuration: Int {
        switch self {
        case .warmup, .stretch: return 300
        case .training: return 180
        case .rest: return 60
        }
    }

    var defaultCustom: Int {
        switch self {
        case .warmup, .stretch: return 600
        case .training: return 240
        case .rest: return 90
        }
    }
}

// MARK: - Text style

enum FontStyle: String, CaseIterable, Identifiable {
    case street, iron

    var id: String { rawValue }

    var fontName: String {
        switch self {
        case .street: return "Bangers-Regular"
        case .iron: return "RussoOne-Regular"
        }
    }

    var label: String {
        switch self {
        case .street: return "Street"
        case .iron: return "Iron"
        }
    }

    /// Horizontal slant in degrees (negative leans right, like a fighting-game logo).
    var skew: Double {
        switch self {
        case .street: return -7
        case .iron: return 0
        }
    }

    var digitWidth: CGFloat { self == .street ? 0.62 : 0.72 }
    var colonWidth: CGFloat { self == .street ? 0.32 : 0.34 }
}

enum Palette: String, CaseIterable, Identifiable {
    case feu, or, glace, acier, venin

    var id: String { rawValue }

    var colors: [Color] {
        switch self {
        case .feu: return [Color(hex: 0xFFF36B), Color(hex: 0xFF9A1F), Color(hex: 0xE0161F)]
        case .or: return [Color(hex: 0xFFFFFF), Color(hex: 0xFFD84A), Color(hex: 0xB8741A)]
        case .glace: return [Color(hex: 0xFFFFFF), Color(hex: 0x7FE3FF), Color(hex: 0x1F5BFF)]
        case .acier: return [Color(hex: 0xFFFFFF), Color(hex: 0xC9CCD6), Color(hex: 0x5D6170)]
        case .venin: return [Color(hex: 0xF4FFB0), Color(hex: 0x7DFF4A), Color(hex: 0x0F9D3A)]
        }
    }

    var stroke: Color {
        switch self {
        case .feu: return Color(hex: 0x1A0505)
        case .or: return Color(hex: 0x1D1203)
        case .glace: return Color(hex: 0x03102A)
        case .acier: return Color(hex: 0x0C0D12)
        case .venin: return Color(hex: 0x031A08)
        }
    }

    var gradient: LinearGradient {
        LinearGradient(
            stops: [
                .init(color: colors[0], location: 0.15),
                .init(color: colors[1], location: 0.55),
                .init(color: colors[2], location: 0.9),
            ],
            startPoint: .top, endPoint: .bottom
        )
    }
}

// MARK: - Fonts used for the UI

enum UIFonts {
    static func ui(_ size: CGFloat, heavy: Bool = true) -> Font {
        .custom(heavy ? "BarlowCondensed-ExtraBold" : "BarlowCondensed-SemiBold", fixedSize: size)
    }
}

// MARK: - Time formatting

enum TimeFormat {
    /// 03:00
    static func clock(_ seconds: Double) -> String {
        let v = max(0, Int(seconds.rounded(.up)))
        return String(format: "%02d:%02d", v / 60, v % 60)
    }

    /// 30s, 3 min, 7:30
    static func short(_ s: Int) -> String {
        if s < 60 { return "\(s)s" }
        if s % 60 == 0 { return "\(s / 60) min" }
        return mss(s)
    }

    /// 7:30
    static func mss(_ s: Int) -> String {
        String(format: "%d:%02d", s / 60, s % 60)
    }
}
