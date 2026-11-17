import AVFoundation

/// Sound themes the user can pick in the menu.
enum SoundPack: String, CaseIterable, Identifiable {
    case boxe, muscu, sifflet, arcade, gong
    var id: String { rawValue }
}

final class SoundPlayer {
    /// go = round starts, stop = round ends (rest), end = session over,
    /// tick = 3-2-1 countdown, warn = 10 seconds left.
    enum Event: String, CaseIterable {
        case go, stop, end, tick, warn
    }

    private var players: [String: AVAudioPlayer] = [:]

    init() {
        // .playback = sounds play even when the silent switch is on.
        // .mixWithOthers = your music keeps playing.
        let session = AVAudioSession.sharedInstance()
        try? session.setCategory(.playback, mode: .default, options: [.mixWithOthers])
        try? session.setActive(true)

        for pack in SoundPack.allCases {
            for ev in Event.allCases {
                let name = "\(pack.rawValue)_\(ev.rawValue)"
                guard let url = Bundle.main.url(forResource: name, withExtension: "wav"),
                      let p = try? AVAudioPlayer(contentsOf: url) else { continue }
                p.prepareToPlay()
                players[name] = p
            }
        }
    }

    func play(_ ev: Event, pack: SoundPack) {
        guard let p = players["\(pack.rawValue)_\(ev.rawValue)"] else { return }
        p.currentTime = 0
        p.play()
    }
}
