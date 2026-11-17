import SwiftUI
import UIKit

/// Runs the countdown. In Training it chains: round → rest → round … → end.
@MainActor
final class TimerEngine: ObservableObject {
    enum State { case idle, running, paused, done }
    enum Phase { case ready, work, rest }

    @Published private(set) var state: State = .idle
    @Published private(set) var phase: Phase = .ready
    @Published private(set) var round = 1
    @Published private(set) var remaining: Double = 0
    @Published private(set) var total: Double = 1
    @Published private(set) var fightUntil = Date.distantPast

    private var endDate = Date()
    private var lastSecond = -1
    private let settings: Settings
    private let sound: SoundPlayer
    private let haptic = UIImpactFeedbackGenerator(style: .heavy)

    init(settings: Settings, sound: SoundPlayer) {
        self.settings = settings
        self.sound = sound
    }

    var isBusy: Bool { state != .idle }

    /// 1 = full ring, 0 = empty.
    var progress: Double {
        switch state {
        case .idle: return 1
        case .done: return 0
        default: return phase == .ready ? 1 : max(0, min(1, remaining / total))
        }
    }

    // MARK: Controls

    func toggle() {
        switch state {
        case .idle, .done: start()
        case .running: pause()
        case .paused: resume()
        }
    }

    func reset() {
        state = .idle
        phase = .ready
        round = 1
        setScreenAwake(false)
    }

    private func start() {
        round = 1
        begin(.ready, seconds: 3)
        state = .running
        setScreenAwake(true)
        haptic.impactOccurred()
    }

    private func pause() {
        remaining = max(0, endDate.timeIntervalSinceNow)
        state = .paused
        setScreenAwake(false)
    }

    private func resume() {
        endDate = Date().addingTimeInterval(remaining)
        lastSecond = -1
        state = .running
        setScreenAwake(true)
    }

    // MARK: Ticking (called ~20 times per second by the view)

    func tick() {
        guard state == .running else { return }
        remaining = max(0, endDate.timeIntervalSinceNow)
        let sec = Int(remaining.rounded(.up))

        if sec != lastSecond {
            lastSecond = sec
            if phase == .ready {
                if sec > 0 { play(.tick) }
            } else {
                if sec == 10 && total > 20 { play(.warn) }
                if sec <= 3 && sec > 0 { play(.tick) }
            }
        }

        if remaining <= 0 { advance() }
    }

    private func advance() {
        let mode = settings.mode
        switch phase {
        case .ready:
            play(.go)
            fightUntil = Date().addingTimeInterval(0.9)
            begin(.work, seconds: settings.duration(for: mode))
        case .work where mode == .training && round < settings.rounds:
            play(.stop)
            begin(.rest, seconds: settings.duration(for: .rest))
        case .rest:
            round += 1
            play(.go)
            begin(.work, seconds: settings.duration(for: .training))
        default:
            finish()
        }
        haptic.impactOccurred()
    }

    private func finish() {
        play(.end)
        remaining = 0
        state = .done
        setScreenAwake(false)
    }

    private func begin(_ p: Phase, seconds: Int) {
        phase = p
        total = Double(max(1, seconds))
        remaining = total
        endDate = Date().addingTimeInterval(total)
        lastSecond = -1
    }

    private func play(_ ev: SoundPlayer.Event) {
        if settings.sound { sound.play(ev, pack: settings.pack) }
    }

    /// Lets the menu play a sample when a sound theme is picked.
    func preview(_ pack: SoundPack) {
        sound.play(.go, pack: pack)
    }

    private func setScreenAwake(_ on: Bool) {
        UIApplication.shared.isIdleTimerDisabled = on
    }
}
