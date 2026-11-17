import SwiftUI

/// Fighting-game title text: gradient fill, thick dark outline, hard drop shadow, slight slant.
struct FightText: View {
    @EnvironmentObject private var settings: Settings

    let text: String
    let size: CGFloat
    /// Give each digit the same width so the clock does not wobble.
    var fixedDigits = false
    /// Force a style (used for the previews in the style sheet).
    var style: FontStyle? = nil

    var body: some View {
        let st = style ?? settings.font
        let pal = settings.palette
        let outline = max(1.5, size * 0.035)
        let slant = CGFloat(tan(st.skew * .pi / 180))

        ZStack {
            ForEach(0..<8, id: \.self) { i in
                let a = Double(i) * .pi / 4
                glyphs(st)
                    .foregroundStyle(pal.stroke)
                    .offset(x: CGFloat(cos(a)) * outline, y: CGFloat(sin(a)) * outline)
            }
            glyphs(st).foregroundStyle(pal.gradient)
        }
        .compositingGroup()
        .shadow(color: .black.opacity(0.6), radius: 0, x: 0, y: size * 0.05)
        .transformEffect(CGAffineTransform(a: 1, b: 0, c: slant, d: 1, tx: -slant * size * 0.55, ty: 0))
    }

    @ViewBuilder
    private func glyphs(_ st: FontStyle) -> some View {
        let font = Font.custom(st.fontName, fixedSize: size)
        if fixedDigits {
            HStack(spacing: 0) {
                ForEach(Array(text.enumerated()), id: \.offset) { _, ch in
                    Text(String(ch))
                        .font(font)
                        .frame(width: size * (ch == ":" ? st.colonWidth : st.digitWidth))
                }
            }
        } else {
            Text(text)
                .font(font)
                .lineLimit(1)
                .minimumScaleFactor(0.4)
        }
    }
}
