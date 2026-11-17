package io.github.jpx2882.ringtimer

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

// Colors

val Ink = Color(0xFF0B0A0F)
val Panel = Color(0xFF16141D)
val Line = Color(0xFF2A2733)
val TextColor = Color(0xFFF4F1FA)
val Muted = Color(0xFF8D88A0)
val TabBarColor = Color(0xFF09080C)

// Fonts

val Bangers = FontFamily(Font(R.font.bangers_regular))
val RussoOne = FontFamily(Font(R.font.russo_one_regular))
val Barlow = FontFamily(
    Font(R.font.barlow_condensed_semibold, FontWeight.SemiBold),
    Font(R.font.barlow_condensed_extrabold, FontWeight.ExtraBold),
)

// Modes

enum class Mode(
    val key: String,
    val color: Color,
    /** The three quick presets (the 4th button is "Custom"). */
    val presets: List<Int>,
    val defaultDuration: Int,
    val defaultCustom: Int,
) {
    WARMUP("warmup", Color(0xFFFF8C1A), listOf(120, 180, 300), 300, 600),   // orange-yellow
    TRAINING("training", Color(0xFFE8202F), listOf(60, 120, 180), 180, 240), // red
    REST("rest", Color(0xFFFFD21F), listOf(30, 45, 60), 60, 90),            // yellow
    STRETCH("stretch", Color(0xFF2F8BFF), listOf(120, 180, 300), 300, 600), // blue
}

// Title font style

enum class TitleFont(
    val key: String,
    val label: String,
    val family: FontFamily,
    /** Slant in degrees (negative leans right, like a fighting-game logo). */
    val skewDeg: Float,
    val digitWidth: Float,
    val colonWidth: Float,
) {
    STREET("street", "Street", Bangers, -7f, 0.62f, 0.32f),
    IRON("iron", "Iron", RussoOne, 0f, 0.72f, 0.34f),
}

// Text colors

enum class TextPalette(val key: String, val colors: List<Color>, val stroke: Color) {
    FEU("feu", listOf(Color(0xFFFFF36B), Color(0xFFFF9A1F), Color(0xFFE0161F)), Color(0xFF1A0505)),
    OR("or", listOf(Color(0xFFFFFFFF), Color(0xFFFFD84A), Color(0xFFB8741A)), Color(0xFF1D1203)),
    GLACE("glace", listOf(Color(0xFFFFFFFF), Color(0xFF7FE3FF), Color(0xFF1F5BFF)), Color(0xFF03102A)),
    ACIER("acier", listOf(Color(0xFFFFFFFF), Color(0xFFC9CCD6), Color(0xFF5D6170)), Color(0xFF0C0D12)),
    VENIN("venin", listOf(Color(0xFFF4FFB0), Color(0xFF7DFF4A), Color(0xFF0F9D3A)), Color(0xFF031A08));

    val brush: Brush
        get() = Brush.verticalGradient(
            0.15f to colors[0],
            0.55f to colors[1],
            0.9f to colors[2],
        )
}

// Sound themes

enum class SoundPack(val key: String) {
    BOXE("boxe"), MUSCU("muscu"), SIFFLET("sifflet"), ARCADE("arcade"), GONG("gong"),
}

// Time formatting

object TimeFormat {
    /** 03:00 */
    fun clock(seconds: Double): String {
        val v = max(0, ceil(seconds).toInt())
        return String.format(Locale.US, "%02d:%02d", v / 60, v % 60)
    }

    /** 30s, 3 min, 7:30 */
    fun short(s: Int): String = when {
        s < 60 -> "${s}s"
        s % 60 == 0 -> "${s / 60} min"
        else -> mss(s)
    }

    /** 7:30 */
    fun mss(s: Int): String = String.format(Locale.US, "%d:%02d", s / 60, s % 60)
}
