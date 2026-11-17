import SwiftUI

/// Everything the user chooses. Each change is saved right away in UserDefaults.
final class Settings: ObservableObject {
    private let store = UserDefaults.standard

    @Published var mode: Mode { didSet { store.set(mode.rawValue, forKey: "mode") } }
    @Published var rounds: Int { didSet { store.set(rounds, forKey: "rounds") } }
    @Published var font: FontStyle { didSet { store.set(font.rawValue, forKey: "font") } }
    @Published var palette: Palette { didSet { store.set(palette.rawValue, forKey: "palette") } }
    @Published var sound: Bool { didSet { store.set(sound, forKey: "sound") } }
    @Published var pack: SoundPack { didSet { store.set(pack.rawValue, forKey: "pack") } }
    @Published var lang: Lang { didSet { store.set(lang.rawValue, forKey: "lang") } }

    /// Keyed by Mode.rawValue.
    @Published private var durations: [String: Int] { didSet { store.set(durations, forKey: "durations") } }
    @Published private var customs: [String: Int] { didSet { store.set(customs, forKey: "customs") } }
    @Published private var customOn: [String: Bool] { didSet { store.set(customOn, forKey: "customOn") } }

    init() {
        let s = UserDefaults.standard
        mode = Mode(rawValue: s.string(forKey: "mode") ?? "") ?? .training
        let r = s.integer(forKey: "rounds")
        rounds = (1...20).contains(r) ? r : 3
        font = FontStyle(rawValue: s.string(forKey: "font") ?? "") ?? .street
        palette = Palette(rawValue: s.string(forKey: "palette") ?? "") ?? .feu
        sound = s.object(forKey: "sound") as? Bool ?? true
        pack = SoundPack(rawValue: s.string(forKey: "pack") ?? "") ?? .boxe
        let deviceIsFrench = Locale.preferredLanguages.first?.lowercased().hasPrefix("fr") ?? false
        lang = Lang(rawValue: s.string(forKey: "lang") ?? "") ?? (deviceIsFrench ? .fr : .en)
        durations = s.dictionary(forKey: "durations") as? [String: Int] ?? [:]
        customs = s.dictionary(forKey: "customs") as? [String: Int] ?? [:]
        customOn = s.dictionary(forKey: "customOn") as? [String: Bool] ?? [:]
    }

    // MARK: Durations

    func duration(for m: Mode) -> Int { durations[m.rawValue] ?? m.defaultDuration }
    func custom(for m: Mode) -> Int { customs[m.rawValue] ?? m.defaultCustom }
    func isCustom(_ m: Mode) -> Bool { customOn[m.rawValue] ?? false }

    func pickPreset(_ seconds: Int, for m: Mode) {
        durations[m.rawValue] = seconds
        customOn[m.rawValue] = false
    }

    func setCustom(_ seconds: Int, for m: Mode) {
        let s = max(5, seconds)
        customs[m.rawValue] = s
        durations[m.rawValue] = s
        customOn[m.rawValue] = true
    }

    // MARK: Text

    func t(_ key: String) -> String { L10n.t(key, lang) }
    func name(_ m: Mode) -> String { t("n_" + m.rawValue) }
    func tabLabel(_ m: Mode) -> String { t("tab_" + m.rawValue) }
}
