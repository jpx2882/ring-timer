import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var settings: Settings
    @EnvironmentObject private var engine: TimerEngine

    @State private var showStyle = false
    @State private var showCustom = false

    private let ticker = Timer.publish(every: 0.05, on: .main, in: .common).autoconnect()

    /// Color of the current phase (rest turns yellow even inside Training).
    private var accent: Color {
        if engine.isBusy && engine.phase == .rest { return Mode.rest.color }
        return settings.mode.color
    }

    var body: some View {
        VStack(spacing: 0) {
            topBar
                .padding(.horizontal, 16)
                .padding(.top, 6)

            stage
                .frame(maxHeight: .infinity)

            controls
                .padding(.horizontal, 16)
                .padding(.bottom, 12)
                .opacity(engine.isBusy ? 0.35 : 1)
                .disabled(engine.isBusy)

            actions
                .padding(.horizontal, 16)
                .padding(.bottom, 12)

            tabBar
        }
        .background(background)
        .onReceive(ticker) { _ in engine.tick() }
        .sheet(isPresented: $showStyle) {
            StyleSheet()
                .environmentObject(settings)
                .environmentObject(engine)
                .presentationDetents([.medium, .large])
        }
        .sheet(isPresented: $showCustom) {
            CustomTimeSheet(mode: settings.mode)
                .environmentObject(settings)
                .presentationDetents([.height(420)])
        }
        .preferredColorScheme(.dark)
    }

    // MARK: - Background

    private var background: some View {
        ZStack {
            Theme.ink
            RadialGradient(colors: [accent.opacity(0.32), .clear],
                           center: UnitPoint(x: 0.5, y: 0.38),
                           startRadius: 0, endRadius: 420)
        }
        .animation(.easeInOut(duration: 0.4), value: accent)
        .ignoresSafeArea()
    }

    // MARK: - Top bar (menu)

    private var topBar: some View {
        HStack {
            Spacer()
            Button { showStyle = true } label: {
                Image(systemName: "line.3.horizontal")
                    .font(.system(size: 20, weight: .bold))
                    .frame(width: 44, height: 44)
                    .foregroundStyle(Theme.text)
                    .background(RoundedRectangle(cornerRadius: 12).fill(Theme.panel))
                    .overlay(RoundedRectangle(cornerRadius: 12).strokeBorder(Theme.line, lineWidth: 1))
            }
            .buttonStyle(.plain)
            .accessibilityLabel(settings.t("style"))
        }
    }

    // MARK: - Stage (banner, info, ring)

    private var stage: some View {
        VStack(spacing: 6) {
            Text(bannerText)
                .font(.custom(settings.font.fontName, fixedSize: 46))
                .foregroundStyle(accent)
                .lineLimit(1)
                .minimumScaleFactor(0.5)
                .shadow(color: .black.opacity(0.6), radius: 0, x: 0, y: 3)
                .transformEffect(settings.font.slant(height: 46))

            Text(infoText)
                .font(UIFonts.ui(20))
                .tracking(2.5)
                .textCase(.uppercase)
                .foregroundStyle(Theme.muted)
                .frame(height: 24)

            ring
        }
        .padding(.horizontal, 16)
    }

    private var bannerText: String {
        let m = settings.mode
        switch engine.state {
        case .idle:
            return settings.name(m)
        case .done:
            return m == .training ? settings.t("gg") : settings.name(m)
        case .running, .paused:
            guard m == .training else { return settings.name(m) }
            return engine.phase == .rest ? settings.name(.rest) : "ROUND \(engine.round)"
        }
    }

    private var infoText: String {
        let m = settings.mode
        switch engine.state {
        case .idle:
            guard m == .training else { return "" }
            return "\(settings.rounds) \(settings.t("rounds")) · \(settings.t("restWord")) \(TimeFormat.short(settings.duration(for: .rest)))"
        case .done:
            return m == .training ? "\(settings.rounds) \(settings.t("roundsDone"))" : settings.t("finished")
        case .running, .paused:
            if engine.phase == .ready { return settings.t("ready") }
            guard m == .training else { return "" }
            if engine.phase == .rest {
                return "\(settings.t("next")) \(engine.round + 1) / \(settings.rounds)"
            }
            return "round \(engine.round) / \(settings.rounds)"
        }
    }

    private var ring: some View {
        GeometryReader { g in
            let d = min(g.size.width, g.size.height)
            let lw = max(8, d * 0.05)
            ZStack {
                Circle()
                    .stroke(Theme.line, lineWidth: lw)
                    .padding(lw / 2)
                Circle()
                    .trim(from: 0, to: engine.progress)
                    .stroke(accent, style: StrokeStyle(lineWidth: lw, lineCap: .round))
                    .rotationEffect(.degrees(-90))
                    .padding(lw / 2)
                    .shadow(color: accent.opacity(0.8), radius: 8)
                    .animation(.linear(duration: 0.1), value: engine.progress)
                clock(diameter: d)
            }
            .frame(width: d, height: d)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .aspectRatio(1, contentMode: .fit)
        .frame(maxWidth: 360)
    }

    private var clockText: String {
        switch engine.state {
        case .idle:
            return TimeFormat.clock(Double(settings.duration(for: settings.mode)))
        case .done:
            return settings.mode == .training ? "K.O.!" : settings.t("done")
        case .running, .paused:
            if engine.phase == .ready { return "\(max(1, Int(engine.remaining.rounded(.up))))" }
            if engine.fightUntil > Date() { return "FIGHT!" }
            return TimeFormat.clock(engine.remaining)
        }
    }

    @ViewBuilder
    private func clock(diameter d: CGFloat) -> some View {
        let text = clockText
        let isTime = text.count == 5 && text.contains(":")
        let size = d * (isTime ? 0.25 : 0.22)
        let warn = engine.state == .running && engine.phase != .ready
            && engine.remaining > 0 && engine.remaining <= 10
        let pulse = warn ? 1 + 0.06 * (engine.remaining - engine.remaining.rounded(.down)) : 1

        FightText(text: text, size: size, fixedDigits: isTime)
            .scaleEffect(pulse)
            .frame(maxWidth: d * 0.8)
    }

    // MARK: - Controls (rounds + presets)

    private var controls: some View {
        let m = settings.mode
        return VStack(spacing: 10) {
            if m == .training {
                HStack(spacing: 14) {
                    stepButton("−", label: settings.t("minus")) {
                        settings.rounds = max(1, settings.rounds - 1)
                    }
                    VStack(spacing: 0) {
                        FightText(text: "\(settings.rounds)", size: 46)
                        Text(settings.t("rounds").uppercased())
                            .font(UIFonts.ui(13))
                            .tracking(2.3)
                            .foregroundStyle(Theme.muted)
                    }
                    .frame(minWidth: 92)
                    stepButton("+", label: settings.t("plus")) {
                        settings.rounds = min(20, settings.rounds + 1)
                    }
                }
            }

            HStack(spacing: 8) {
                ForEach(m.presets, id: \.self) { s in
                    let on = !settings.isCustom(m) && settings.duration(for: m) == s
                    Button { settings.pickPreset(s, for: m) } label: {
                        Text(TimeFormat.short(s))
                            .font(UIFonts.ui(22))
                            .frame(maxWidth: .infinity, minHeight: 50)
                    }
                    .buttonStyle(ChipStyle(on: on, accent: accent))
                }

                Button { showCustom = true } label: {
                    VStack(spacing: 1) {
                        Text(settings.t("custom").uppercased())
                            .font(UIFonts.ui(11))
                            .tracking(1.5)
                            .foregroundStyle(settings.isCustom(m) ? accent : Theme.muted)
                        Text(TimeFormat.mss(settings.custom(for: m)))
                            .font(UIFonts.ui(19))
                    }
                    .frame(maxWidth: .infinity, minHeight: 50)
                }
                .buttonStyle(ChipStyle(on: settings.isCustom(m), accent: accent, dashed: true))
                .accessibilityLabel(settings.t("customTime"))
            }
        }
    }

    private func stepButton(_ symbol: String, label: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(symbol)
                .font(UIFonts.ui(30))
                .frame(width: 56, height: 56)
        }
        .buttonStyle(RoundStepStyle())
        .accessibilityLabel(label)
    }

    // MARK: - Actions (reset + start)

    private var goLabel: String {
        switch engine.state {
        case .idle: return settings.t("start")
        case .running: return settings.t("pause")
        case .paused: return settings.t("resume")
        case .done: return settings.t("again")
        }
    }

    private var actions: some View {
        HStack(spacing: 10) {
            Button { engine.reset() } label: {
                Image(systemName: "arrow.counterclockwise")
                    .font(.system(size: 26, weight: .bold))
                    .frame(width: 68, height: 68)
            }
            .buttonStyle(PanelButtonStyle())
            .accessibilityLabel(settings.t("reset"))

            Button { engine.toggle() } label: {
                Text(goLabel)
                    .font(.custom(settings.font.fontName, fixedSize: 34))
                    .tracking(2)
                    .foregroundStyle(Theme.ink)
                    .transformEffect(settings.font.slant(height: 34))
                    .frame(maxWidth: .infinity, minHeight: 68)
            }
            .buttonStyle(GoButtonStyle(color: accent))
        }
    }

    // MARK: - Mode tabs

    private var tabBar: some View {
        HStack(spacing: 6) {
            ForEach(Mode.allCases) { m in
                let selected = settings.mode == m
                Button {
                    if !engine.isBusy { settings.mode = m }
                } label: {
                    VStack(spacing: 5) {
                        Capsule()
                            .fill(m.color.opacity(selected ? 1 : 0.45))
                            .frame(width: selected ? 36 : 26, height: 6)
                        Text(settings.tabLabel(m).uppercased())
                            .font(UIFonts.ui(settings.lang == .fr ? 13 : 14))
                            .tracking(settings.lang == .fr ? 0.3 : 1.1)
                            .lineLimit(1)
                            .minimumScaleFactor(0.7)
                    }
                    .foregroundStyle(m.color)
                    .frame(maxWidth: .infinity, minHeight: 62)
                    .background(RoundedRectangle(cornerRadius: 14).fill(selected ? m.color.opacity(0.2) : Color.clear))
                    .contentShape(Rectangle())
                }
                .buttonStyle(.plain)
                .opacity(engine.isBusy && !selected ? 0.35 : 1)
                .accessibilityAddTraits(selected ? .isSelected : [])
            }
        }
        .padding(.horizontal, 10)
        .padding(.top, 8)
        .padding(.bottom, 4)
        .background(Theme.tabBar.ignoresSafeArea(edges: .bottom))
        .overlay(alignment: .top) {
            Rectangle().fill(Theme.line).frame(height: 1)
        }
    }
}
