package io.github.jpx2882.ringtimer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import java.util.Locale

@Composable
private fun SectionTitle(text: String) {
    Text(
        text.uppercase(),
        style = ui(15f).copy(color = Muted, letterSpacing = 0.18.em),
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
    )
}

@Composable
private fun WideButton(label: String, fill: Color?, textColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier
            .height(54.dp)
            .clip(shape)
            .then(if (fill != null) Modifier.background(fill) else Modifier.border(2.dp, Line, shape))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = ui(20f).copy(color = textColor, letterSpacing = 0.1.em))
    }
}

// Menu: language, font, color, sounds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSheet(settings: Settings, engine: TimerEngine, onClose: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accent = settings.mode.color

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Panel,
        contentColor = TextColor,
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Language
            SectionTitle(settings.t("language"))
            val segShape = RoundedCornerShape(14.dp)
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(segShape)
                    .background(Ink)
                    .border(2.dp, Line, segShape)
            ) {
                Lang.entries.forEach { l ->
                    val on = settings.lang == l
                    Box(
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .background(if (on) TextColor else Color.Transparent)
                            .clickable { settings.lang = l },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (l == Lang.FR) "Français" else "English",
                            style = ui(18f).copy(color = if (on) Ink else Muted),
                        )
                    }
                }
            }

            // Font
            SectionTitle(settings.t("font"))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TitleFont.entries.forEach { f ->
                    val on = settings.font == f
                    val shape = RoundedCornerShape(16.dp)
                    Column(
                        Modifier
                            .weight(1f)
                            .height(96.dp)
                            .clip(shape)
                            .background(Ink)
                            .border(2.dp, if (on) Color.White else Line, shape)
                            .clickable { settings.font = f },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        FightText("FIGHT!", if (f == TitleFont.STREET) 32.dp else 26.dp, settings, font = f)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            f.label.uppercase(),
                            style = ui(13f).copy(color = if (on) TextColor else Muted, letterSpacing = 0.15.em),
                        )
                    }
                }
            }

            // Text color
            SectionTitle(settings.t("color"))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TextPalette.entries.forEach { p ->
                    val on = settings.palette == p
                    val shape = RoundedCornerShape(14.dp)
                    Column(
                        Modifier
                            .weight(1f)
                            .clickable { settings.palette = p },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(shape)
                                .background(Brush.verticalGradient(p.colors))
                                .border(3.dp, if (on) Color.White else Color.Transparent, shape)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            settings.t("p_${p.key}").uppercase(),
                            style = ui(12f).copy(color = if (on) TextColor else Muted, letterSpacing = 0.08.em),
                            maxLines = 1,
                        )
                    }
                }
            }

            // Sounds
            SectionTitle(settings.t("sound"))
            val options: List<SoundPack?> = SoundPack.entries + listOf(null) // null = OFF
            options.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { p ->
                        val on = if (p == null) !settings.sound else settings.sound && settings.pack == p
                        Chip(
                            on = on, accent = accent, modifier = Modifier.weight(1f), height = 48.dp,
                            onClick = {
                                if (p == null) {
                                    settings.sound = false
                                } else {
                                    settings.sound = true
                                    settings.pack = p
                                    engine.preview(p)
                                }
                            },
                        ) {
                            Text(settings.t(if (p == null) "s_off" else "s_${p.key}"), style = ui(19f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            WideButton(settings.t("close"), TextColor, Ink, Modifier.fillMaxWidth(), onClose)
        }
    }
}

// Custom time picker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimeSheet(settings: Settings, mode: Mode, onClose: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val start = settings.custom(mode)
    var total by remember { mutableIntStateOf(start) }

    fun add(delta: Int) {
        total = (total + delta).coerceIn(5, 3600)
    }

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        containerColor = Panel,
        contentColor = TextColor,
    ) {
        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text(
                "${settings.t("customTime")} · ${settings.name(mode)}".uppercase(),
                style = ui(15f).copy(color = Muted, letterSpacing = 0.16.em),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TimeColumn(settings, settings.t("min"), "${total / 60}", Modifier.weight(1f), { add(-60) }, { add(60) })
                FightText(":", 52.dp, settings)
                TimeColumn(
                    settings, settings.t("sec"), String.format(Locale.US, "%02d", total % 60), Modifier.weight(1f),
                    { add(-5) }, { add(5) },
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WideButton(settings.t("cancel"), null, TextColor, Modifier.weight(1f), onClose)
                WideButton(settings.t("ok"), mode.color, Ink, Modifier.weight(2f)) {
                    settings.setCustom(total, mode)
                    onClose()
                }
            }
        }
    }
}

@Composable
private fun TimeColumn(
    settings: Settings,
    title: String,
    value: String,
    modifier: Modifier,
    minus: () -> Unit,
    plus: () -> Unit,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title.uppercase(), style = ui(13f).copy(color = Muted, letterSpacing = 0.18.em))
        Box(Modifier.height(80.dp), contentAlignment = Alignment.Center) {
            FightText(value, 60.dp, settings)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RepeatButton("−", minus)
            RepeatButton("+", plus)
        }
    }
}
