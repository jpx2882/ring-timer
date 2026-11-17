import Foundation

enum Lang: String, CaseIterable, Identifiable {
    case en, fr
    var id: String { rawValue }
}

enum L10n {
    static func t(_ key: String, _ lang: Lang) -> String {
        let table = lang == .fr ? fr : en
        return table[key] ?? en[key] ?? key
    }

    private static let fr: [String: String] = [
        "n_warmup": "ÉCHAUFFEMENT", "n_training": "ENTRAÎNEMENT", "n_rest": "REPOS", "n_stretch": "ÉTIREMENTS",
        "tab_warmup": "Échauffement", "tab_training": "Entraînement", "tab_rest": "Repos", "tab_stretch": "Étirements",
        "font": "Police", "color": "Couleur", "sound": "Sons", "language": "Langue",
        "rounds": "rounds", "restWord": "repos",
        "next": "prochain : round", "ready": "prêt ?", "done": "FINI !", "gg": "BIEN JOUÉ",
        "roundsDone": "rounds terminés", "finished": "terminé",
        "custom": "Perso", "customTime": "Temps perso", "min": "Minutes", "sec": "Secondes",
        "cancel": "ANNULER", "ok": "VALIDER", "close": "OK",
        "start": "START", "pause": "PAUSE", "resume": "REPRENDRE", "again": "ENCORE",
        "p_feu": "Feu", "p_or": "Or", "p_glace": "Glace", "p_acier": "Acier", "p_venin": "Venin",
        "s_boxe": "Boxe", "s_muscu": "Muscu", "s_sifflet": "Sifflet", "s_arcade": "Arcade", "s_gong": "Gong", "s_off": "OFF",
        "style": "Menu", "reset": "Remettre à zéro", "minus": "Moins de rounds", "plus": "Plus de rounds",
    ]

    private static let en: [String: String] = [
        "n_warmup": "WARM UP", "n_training": "TRAINING", "n_rest": "REST", "n_stretch": "STRETCHING",
        "tab_warmup": "Warm up", "tab_training": "Training", "tab_rest": "Rest", "tab_stretch": "Stretching",
        "font": "Font", "color": "Color", "sound": "Sounds", "language": "Language",
        "rounds": "rounds", "restWord": "rest",
        "next": "next: round", "ready": "ready?", "done": "DONE!", "gg": "GOOD JOB",
        "roundsDone": "rounds done", "finished": "finished",
        "custom": "Custom", "customTime": "Custom time", "min": "Minutes", "sec": "Seconds",
        "cancel": "CANCEL", "ok": "SET", "close": "OK",
        "start": "START", "pause": "PAUSE", "resume": "RESUME", "again": "AGAIN",
        "p_feu": "Fire", "p_or": "Gold", "p_glace": "Ice", "p_acier": "Steel", "p_venin": "Venom",
        "s_boxe": "Boxing", "s_muscu": "Gym", "s_sifflet": "Whistle", "s_arcade": "Arcade", "s_gong": "Gong", "s_off": "OFF",
        "style": "Menu", "reset": "Reset", "minus": "Fewer rounds", "plus": "More rounds",
    ]
}
