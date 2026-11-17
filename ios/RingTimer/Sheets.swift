import SwiftUI

// MARK: - Text style sheet (font, color, sound)

struct StyleSheet: View {
    @EnvironmentObject private var settings: Settings
    @EnvironmentObject private var engine: TimerEngine
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 14) {
                sectionTitle(settings.t("language"))
                HStack(spacing: 0) {
                    ForEach(Lang.allCases) { l in
                        let on = settings.lang == l
                        Button { settings.lang = l } label: {
                            Text(l == .fr ? "Français" : "English")
                                .font(UIFonts.ui(18))
                                .tracking(1)
                                .frame(maxWidth: .infinity, minHeight: 50)
                                .foregroundStyle(on ? Theme.ink : Theme.muted)
                                .background(on ? Theme.text : Color.clear)
                        }
                        .buttonStyle(.plain)
                    }
                }
                .background(Theme.ink)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(RoundedRectangle(cornerRadius: 14).strokeBorder(Theme.line, lineWidth: 2))

                sectionTitle(settings.t("font"))
                HStack(spacing: 10) {
                    ForEach(FontStyle.allCases) { f in
                        let on = settings.font == f
                        Button { settings.font = f } label: {
                            VStack(spacing: 6) {
                                FightText(text: "FIGHT!", size: f == .street ? 36 : 30, style: f)
                                Text(f.label.uppercased())
                                    .font(UIFonts.ui(13))
                                    .tracking(2)
                                    .foregroundStyle(on ? Theme.text : Theme.muted)
                            }
                            .frame(maxWidth: .infinity, minHeight: 96)
                            .background(RoundedRectangle(cornerRadius: 16).fill(Theme.ink))
                            .overlay(RoundedRectangle(cornerRadius: 16)
                                .strokeBorder(on ? Color.white : Theme.line, lineWidth: 2))
                        }
                        .buttonStyle(.plain)
                    }
                }

                sectionTitle(settings.t("color"))
                HStack(spacing: 10) {
                    ForEach(Palette.allCases) { p in
                        let on = settings.palette == p
                        Button { settings.palette = p } label: {
                            VStack(spacing: 6) {
                                RoundedRectangle(cornerRadius: 14)
                                    .fill(LinearGradient(colors: p.colors, startPoint: .top, endPoint: .bottom))
                                    .aspectRatio(1, contentMode: .fit)
                                    .overlay(RoundedRectangle(cornerRadius: 14)
                                        .strokeBorder(on ? Color.white : Color.clear, lineWidth: 3))
                                Text(settings.t("p_" + p.rawValue).uppercased())
                                    .font(UIFonts.ui(12))
                                    .tracking(1)
                                    .foregroundStyle(on ? Theme.text : Theme.muted)
                            }
                            .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.plain)
                        .accessibilityLabel(settings.t("p_" + p.rawValue))
                    }
                }

                sectionTitle(settings.t("sound"))
                LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 8), count: 3), spacing: 8) {
                    ForEach(SoundPack.allCases) { p in
                        Button {
                            settings.sound = true
                            settings.pack = p
                            engine.preview(p)
                        } label: {
                            Text(settings.t("s_" + p.rawValue))
                                .font(UIFonts.ui(19))
                                .frame(maxWidth: .infinity, minHeight: 48)
                        }
                        .buttonStyle(ChipStyle(on: settings.sound && settings.pack == p, accent: settings.mode.color))
                    }
                    Button { settings.sound = false } label: {
                        Text(settings.t("s_off"))
                            .font(UIFonts.ui(19))
                            .frame(maxWidth: .infinity, minHeight: 48)
                    }
                    .buttonStyle(ChipStyle(on: !settings.sound, accent: settings.mode.color))
                }
                .padding(.bottom, 4)

                Button { dismiss() } label: {
                    Text(settings.t("close"))
                        .font(UIFonts.ui(20))
                        .tracking(2)
                        .frame(maxWidth: .infinity, minHeight: 54)
                }
                .buttonStyle(SolidButtonStyle(fill: Theme.text, text: Theme.ink))
            }
            .padding(20)
        }
        .foregroundStyle(Theme.text)
        .background(Theme.panel.ignoresSafeArea())
        .preferredColorScheme(.dark)
    }

    private func sectionTitle(_ s: String) -> some View {
        Text(s.uppercased())
            .font(UIFonts.ui(15))
            .tracking(2.7)
            .foregroundStyle(Theme.muted)
    }
}

// MARK: - Custom time picker

struct CustomTimeSheet: View {
    @EnvironmentObject private var settings: Settings
    @Environment(\.dismiss) private var dismiss

    let mode: Mode

    @State private var minutes = 0
    @State private var seconds = 0

    var body: some View {
        VStack(spacing: 18) {
            Text("\(settings.t("customTime")) · \(settings.name(mode))".uppercased())
                .font(UIFonts.ui(15))
                .tracking(2.5)
                .foregroundStyle(Theme.muted)
                .frame(maxWidth: .infinity, alignment: .leading)

            HStack(alignment: .center, spacing: 4) {
                column(title: settings.t("min"), value: "\(minutes)",
                       minus: { add(-60) }, plus: { add(60) })
                FightText(text: ":", size: 56)
                column(title: settings.t("sec"), value: String(format: "%02d", seconds),
                       minus: { add(-5) }, plus: { add(5) })
            }

            HStack(spacing: 10) {
                Button { dismiss() } label: {
                    Text(settings.t("cancel"))
                        .font(UIFonts.ui(20))
                        .tracking(2)
                        .frame(maxWidth: .infinity, minHeight: 54)
                }
                .buttonStyle(OutlineButtonStyle())

                Button {
                    settings.setCustom(minutes * 60 + seconds, for: mode)
                    dismiss()
                } label: {
                    Text(settings.t("ok"))
                        .font(UIFonts.ui(20))
                        .tracking(2)
                        .frame(maxWidth: .infinity, minHeight: 54)
                }
                .buttonStyle(SolidButtonStyle(fill: mode.color, text: Theme.ink))
                .frame(maxWidth: .infinity)
                .layoutPriority(1)
            }
        }
        .padding(20)
        .frame(maxHeight: .infinity, alignment: .top)
        .foregroundStyle(Theme.text)
        .background(Theme.panel.ignoresSafeArea())
        .preferredColorScheme(.dark)
        .onAppear {
            let c = settings.custom(for: mode)
            minutes = c / 60
            seconds = c % 60
        }
    }

    /// Adds (or removes) seconds, between 5 s and 60 min.
    private func add(_ delta: Int) {
        let total = min(3600, max(5, minutes * 60 + seconds + delta))
        minutes = total / 60
        seconds = total % 60
    }

    private func column(title: String, value: String,
                        minus: @escaping () -> Void, plus: @escaping () -> Void) -> some View {
        VStack(spacing: 8) {
            Text(title.uppercased())
                .font(UIFonts.ui(13))
                .tracking(2.3)
                .foregroundStyle(Theme.muted)
            FightText(text: value, size: 64)
                .frame(height: 76)
            HStack(spacing: 10) {
                Button(action: minus) {
                    Text("−").font(UIFonts.ui(30)).frame(width: 56, height: 56)
                }
                .buttonStyle(RoundStepStyle())
                .buttonRepeatBehavior(.enabled)

                Button(action: plus) {
                    Text("+").font(UIFonts.ui(30)).frame(width: 56, height: 56)
                }
                .buttonStyle(RoundStepStyle())
                .buttonRepeatBehavior(.enabled)
            }
        }
        .frame(maxWidth: .infinity)
    }
}
