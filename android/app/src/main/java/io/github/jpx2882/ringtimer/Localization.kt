package io.github.jpx2882.ringtimer

enum class Lang(val key: String) { EN("en"), FR("fr") }

object L10n {
    fun t(key: String, lang: Lang): String {
        val table = if (lang == Lang.FR) fr else en
        return table[key] ?: en[key] ?: key
    }

    private val fr = mapOf(
        "n_warmup" to "ÉCHAUFFEMENT", "n_training" to "ENTRAÎNEMENT", "n_rest" to "REPOS", "n_stretch" to "ÉTIREMENTS",
        "tab_warmup" to "Échauffement", "tab_training" to "Entraînement", "tab_rest" to "Repos", "tab_stretch" to "Étirements",
        "font" to "Police", "color" to "Couleur", "sound" to "Sons", "language" to "Langue",
        "rounds" to "rounds", "restWord" to "repos",
        "next" to "prochain : round", "ready" to "prêt ?", "done" to "FINI !", "gg" to "BIEN JOUÉ",
        "roundsDone" to "rounds terminés", "finished" to "terminé",
        "custom" to "Perso", "customTime" to "Temps perso", "min" to "Minutes", "sec" to "Secondes",
        "cancel" to "ANNULER", "ok" to "VALIDER", "close" to "OK",
        "start" to "START", "pause" to "PAUSE", "resume" to "REPRENDRE", "again" to "ENCORE",
        "p_feu" to "Feu", "p_or" to "Or", "p_glace" to "Glace", "p_acier" to "Acier", "p_venin" to "Venin",
        "s_boxe" to "Boxe", "s_muscu" to "Muscu", "s_sifflet" to "Sifflet", "s_arcade" to "Arcade", "s_gong" to "Gong", "s_off" to "OFF",
        "style" to "Menu", "reset" to "Remettre à zéro", "minus" to "Moins de rounds", "plus" to "Plus de rounds",
    )

    private val en = mapOf(
        "n_warmup" to "WARM UP", "n_training" to "TRAINING", "n_rest" to "REST", "n_stretch" to "STRETCHING",
        "tab_warmup" to "Warm up", "tab_training" to "Training", "tab_rest" to "Rest", "tab_stretch" to "Stretching",
        "font" to "Font", "color" to "Color", "sound" to "Sounds", "language" to "Language",
        "rounds" to "rounds", "restWord" to "rest",
        "next" to "next: round", "ready" to "ready?", "done" to "DONE!", "gg" to "GOOD JOB",
        "roundsDone" to "rounds done", "finished" to "finished",
        "custom" to "Custom", "customTime" to "Custom time", "min" to "Minutes", "sec" to "Seconds",
        "cancel" to "CANCEL", "ok" to "SET", "close" to "OK",
        "start" to "START", "pause" to "PAUSE", "resume" to "RESUME", "again" to "AGAIN",
        "p_feu" to "Fire", "p_or" to "Gold", "p_glace" to "Ice", "p_acier" to "Steel", "p_venin" to "Venom",
        "s_boxe" to "Boxing", "s_muscu" to "Gym", "s_sifflet" to "Whistle", "s_arcade" to "Arcade", "s_gong" to "Gong", "s_off" to "OFF",
        "style" to "Menu", "reset" to "Reset", "minus" to "Fewer rounds", "plus" to "More rounds",
    )
}
