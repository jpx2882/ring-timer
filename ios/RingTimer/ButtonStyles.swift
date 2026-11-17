import SwiftUI

extension FontStyle {
    /// Slants text like a fighting-game logo, kept roughly centered.
    func slant(height: CGFloat) -> CGAffineTransform {
        let c = CGFloat(tan(skew * .pi / 180))
        return CGAffineTransform(a: 1, b: 0, c: c, d: 1, tx: -c * height * 0.55, ty: 0)
    }
}

/// Preset / custom time chips.
struct ChipStyle: ButtonStyle {
    var on: Bool
    var accent: Color
    var dashed = false

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundStyle(on ? Color.white : Theme.text)
            .background(
                RoundedRectangle(cornerRadius: 12)
                    .fill(Theme.panel)
                    .overlay(RoundedRectangle(cornerRadius: 12).fill(on ? accent.opacity(0.22) : Color.clear))
            )
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .strokeBorder(on ? accent : Theme.line,
                                  style: StrokeStyle(lineWidth: 2, dash: dashed && !on ? [6, 4] : []))
            )
            .scaleEffect(configuration.isPressed ? 0.95 : 1)
    }
}

/// Round − / + buttons.
struct RoundStepStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundStyle(Theme.text)
            .background(Circle().fill(Theme.panel))
            .overlay(Circle().strokeBorder(Theme.line, lineWidth: 2))
            .scaleEffect(configuration.isPressed ? 0.92 : 1)
    }
}

/// Dark square button (reset, etc.).
struct PanelButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundStyle(Theme.text)
            .background(RoundedRectangle(cornerRadius: 18).fill(Theme.panel))
            .overlay(RoundedRectangle(cornerRadius: 18).strokeBorder(Theme.line, lineWidth: 2))
            .scaleEffect(configuration.isPressed ? 0.95 : 1)
    }
}

/// Big START / PAUSE button, arcade style with a solid bottom edge.
struct GoButtonStyle: ButtonStyle {
    var color: Color

    func makeBody(configuration: Configuration) -> some View {
        let pressed = configuration.isPressed
        return configuration.label
            .background(RoundedRectangle(cornerRadius: 18).fill(color))
            .background(
                RoundedRectangle(cornerRadius: 18)
                    .fill(color)
                    .overlay(RoundedRectangle(cornerRadius: 18).fill(Color.black.opacity(0.45)))
                    .offset(y: pressed ? 3 : 6)
            )
            .offset(y: pressed ? 3 : 0)
            .animation(.easeOut(duration: 0.08), value: pressed)
    }
}

/// Full-width filled button in sheets.
struct SolidButtonStyle: ButtonStyle {
    var fill: Color
    var text: Color

    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundStyle(text)
            .background(RoundedRectangle(cornerRadius: 14).fill(fill))
            .opacity(configuration.isPressed ? 0.8 : 1)
    }
}

/// Full-width outlined button in sheets.
struct OutlineButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .foregroundStyle(Theme.text)
            .overlay(RoundedRectangle(cornerRadius: 14).strokeBorder(Theme.line, lineWidth: 2))
            .opacity(configuration.isPressed ? 0.7 : 1)
    }
}
