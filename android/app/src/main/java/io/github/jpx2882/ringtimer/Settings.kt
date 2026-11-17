package io.github.jpx2882.ringtimer

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf

/** Everything the user chooses. Each change is saved right away. */
class Settings(context: Context) {
    private val prefs = context.getSharedPreferences("ringtimer", Context.MODE_PRIVATE)

    private val modeState = mutableStateOf(
        Mode.entries.find { it.key == prefs.getString("mode", null) } ?: Mode.TRAINING
    )
    var mode: Mode
        get() = modeState.value
        set(v) {
            modeState.value = v
            prefs.edit().putString("mode", v.key).apply()
        }

    private val roundsState = mutableIntStateOf(prefs.getInt("rounds", 3).coerceIn(1, 20))
    var rounds: Int
        get() = roundsState.intValue
        set(v) {
            val r = v.coerceIn(1, 20)
            roundsState.intValue = r
            prefs.edit().putInt("rounds", r).apply()
        }

    private val fontState = mutableStateOf(
        TitleFont.entries.find { it.key == prefs.getString("font", null) } ?: TitleFont.STREET
    )
    var font: TitleFont
        get() = fontState.value
        set(v) {
            fontState.value = v
            prefs.edit().putString("font", v.key).apply()
        }

    private val paletteState = mutableStateOf(
        TextPalette.entries.find { it.key == prefs.getString("palette", null) } ?: TextPalette.FEU
    )
    var palette: TextPalette
        get() = paletteState.value
        set(v) {
            paletteState.value = v
            prefs.edit().putString("palette", v.key).apply()
        }

    private val soundState = mutableStateOf(prefs.getBoolean("sound", true))
    var sound: Boolean
        get() = soundState.value
        set(v) {
            soundState.value = v
            prefs.edit().putBoolean("sound", v).apply()
        }

    private val packState = mutableStateOf(
        SoundPack.entries.find { it.key == prefs.getString("pack", null) } ?: SoundPack.BOXE
    )
    var pack: SoundPack
        get() = packState.value
        set(v) {
            packState.value = v
            prefs.edit().putString("pack", v.key).apply()
        }

    private val langState = mutableStateOf(
        Lang.entries.find { it.key == prefs.getString("lang", null) } ?: Lang.EN
    )
    var lang: Lang
        get() = langState.value
        set(v) {
            langState.value = v
            prefs.edit().putString("lang", v.key).apply()
        }

    // Durations, keyed by Mode.key

    private val durations = mutableStateMapOf<String, Int>().apply {
        Mode.entries.forEach { m -> prefs.getInt("dur_${m.key}", -1).takeIf { it > 0 }?.let { put(m.key, it) } }
    }
    private val customs = mutableStateMapOf<String, Int>().apply {
        Mode.entries.forEach { m -> prefs.getInt("custom_${m.key}", -1).takeIf { it > 0 }?.let { put(m.key, it) } }
    }
    private val customOn = mutableStateMapOf<String, Boolean>().apply {
        Mode.entries.forEach { m -> put(m.key, prefs.getBoolean("customOn_${m.key}", false)) }
    }

    fun duration(m: Mode): Int = durations[m.key] ?: m.defaultDuration
    fun custom(m: Mode): Int = customs[m.key] ?: m.defaultCustom
    fun isCustom(m: Mode): Boolean = customOn[m.key] ?: false

    fun pickPreset(seconds: Int, m: Mode) {
        durations[m.key] = seconds
        customOn[m.key] = false
        prefs.edit()
            .putInt("dur_${m.key}", seconds)
            .putBoolean("customOn_${m.key}", false)
            .apply()
    }

    fun setCustom(seconds: Int, m: Mode) {
        val s = seconds.coerceIn(5, 3600)
        customs[m.key] = s
        durations[m.key] = s
        customOn[m.key] = true
        prefs.edit()
            .putInt("custom_${m.key}", s)
            .putInt("dur_${m.key}", s)
            .putBoolean("customOn_${m.key}", true)
            .apply()
    }

    // Text

    fun t(key: String): String = L10n.t(key, lang)
    fun name(m: Mode): String = t("n_${m.key}")
    fun tabLabel(m: Mode): String = t("tab_${m.key}")
}
