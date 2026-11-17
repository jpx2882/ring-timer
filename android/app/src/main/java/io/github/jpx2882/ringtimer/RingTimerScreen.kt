package io.github.jpx2882.ringtimer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.min
import kotlinx.coroutines.delay
import kotlin.math.floor

@Composable
fun RingTimerScreen(settings: Settings, engine: TimerEngine) {
    var showMenu by remember { mutableStateOf(false) }
    var showCustom by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    SideEffect { engine.onBeat = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) } }

    // Drives the countdown.
    LaunchedEffect(Unit) {
        while (true) {
            engine.tick()
            delay(50)
        }
    }

    // Rest turns yellow even inside Training.
    val target = if (engine.isBusy && engine.phase == TimerEngine.Phase.REST) Mode.REST.color else settings.mode.color
    val accent by animateColorAsState(target, tween(400), label = "accent")

    Box(
        Modifier
            .fillMaxSize()
            .background(Ink)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        listOf(accent.copy(alpha = 0.32f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height * 0.38f),
                        radius = size.width * 1.1f,
                    )
                )
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(1f)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            ) {
                TopBar(settings) { showMenu = true }
                Stage(settings, engine, accent, Modifier.weight(1f))
                Controls(settings, engine, accent) { showCustom = true }
                Actions(settings, engine, accent)
            }
            TabBar(settings, engine)
        }
    }

    if (showMenu) MenuSheet(settings, engine) { showMenu = false }
    if (showCustom) CustomTimeSheet(settings, settings.mode) { showCustom = false }
}

// Top bar

@Composable
private fun TopBar(settings: Settings, onMenu: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(shape)
                .background(Panel)
                .border(1.dp, Line, shape)
                .clickable(onClick = onMenu)
                .semantics { contentDescription = settings.t("style") },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Menu, contentDescription = null, tint = TextColor)
        }
    }
}

// Stage: banner, info line, ring

@Composable
private fun Stage(settings: Settings, engine: TimerEngine, accent: Color, modifier: Modifier) {
    val banner = bannerText(settings, engine)
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            banner,
            style = TextStyle(
                fontFamily = settings.font.family,
                fontSize = dpSp(if (banner.length > 10) 38f else 46f),
                color = accent,
                shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(0f, 6f), 0f),
            ),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.skew(settings.font.skewDeg),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            infoText(settings, engine).uppercase(),
            style = ui(20f).copy(color = Muted, letterSpacing = 0.12.em),
            maxLines = 1,
            softWrap = false,
            modifier = Modifier.height(26.dp),
        )
        Spacer(Modifier.height(6.dp))
        BoxWithConstraints(
            Modifier
                .weight(1f, fill = false)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val d = min(min(maxWidth, maxHeight), 360.dp)
            Ring(settings, engine, accent, d)
        }
    }
}

@Composable
private fun Ring(settings: Settings, engine: TimerEngine, accent: Color, d: Dp) {
    val progress by animateFloatAsState(engine.progress, tween(100, easing = LinearEasing), label = "ring")
    val lw = maxOf(8.dp, d * 0.05f)

    Box(Modifier.size(d), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = lw.toPx()
            val glow = stroke * 1.9f
            val inset = glow / 2f
            val tl = Offset(inset, inset)
            val arc = Size(size.width - glow, size.height - glow)
            drawArc(Line, 0f, 360f, false, tl, arc, style = Stroke(stroke))
            val sweep = 360f * progress
            if (sweep > 0.5f) {
                drawArc(accent.copy(alpha = 0.22f), -90f, sweep, false, tl, arc, style = Stroke(glow, cap = StrokeCap.Round))
                drawArc(accent, -90f, sweep, false, tl, arc, style = Stroke(stroke, cap = StrokeCap.Round))
            }
        }
        Clock(settings, engine, d)
    }
}

@Composable
private fun Clock(settings: Settings, engine: TimerEngine, d: Dp) {
    val text = clockText(settings, engine)
    val isTime = text.length == 5 && text[2] == ':'
    val size = d * (if (isTime) 0.25f else 0.22f)
    val warn = engine.state == TimerEngine.State.RUNNING && engine.phase != TimerEngine.Phase.READY &&
        engine.remaining > 0.0 && engine.remaining <= 10.0
    val pulse = if (warn) 1f + 0.06f * (engine.remaining - floor(engine.remaining)).toFloat() else 1f

    FightText(
        text, size, settings,
        fixedDigits = isTime,
        modifier = Modifier
            .widthIn(max = d * 0.84f)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            },
    )
}

private fun bannerText(settings: Settings, engine: TimerEngine): String {
    val m = settings.mode
    return when (engine.state) {
        TimerEngine.State.IDLE -> settings.name(m)
        TimerEngine.State.DONE -> if (m == Mode.TRAINING) settings.t("gg") else settings.name(m)
        else -> when {
            m != Mode.TRAINING -> settings.name(m)
            engine.phase == TimerEngine.Phase.REST -> settings.name(Mode.REST)
            else -> "ROUND ${engine.round}"
        }
    }
}

private fun infoText(settings: Settings, engine: TimerEngine): String {
    val m = settings.mode
    return when (engine.state) {
        TimerEngine.State.IDLE ->
            if (m == Mode.TRAINING)
                "${settings.rounds} ${settings.t("rounds")} · ${settings.t("restWord")} ${TimeFormat.short(settings.duration(Mode.REST))}"
            else ""
        TimerEngine.State.DONE ->
            if (m == Mode.TRAINING) "${settings.rounds} ${settings.t("roundsDone")}" else settings.t("finished")
        else -> when {
            engine.phase == TimerEngine.Phase.READY -> settings.t("ready")
            m != Mode.TRAINING -> ""
            engine.phase == TimerEngine.Phase.REST -> "${settings.t("next")} ${engine.round + 1} / ${settings.rounds}"
            else -> "round ${engine.round} / ${settings.rounds}"
        }
    }
}

private fun clockText(settings: Settings, engine: TimerEngine): String = when (engine.state) {
    TimerEngine.State.IDLE -> TimeFormat.clock(settings.duration(settings.mode).toDouble())
    TimerEngine.State.DONE -> if (settings.mode == Mode.TRAINING) "K.O.!" else settings.t("done")
    else -> when {
        engine.phase == TimerEngine.Phase.READY -> "${maxOf(1, kotlin.math.ceil(engine.remaining).toInt())}"
        engine.isShowingFight() -> "FIGHT!"
        else -> TimeFormat.clock(engine.remaining)
    }
}

// Controls: rounds + presets

@Composable
private fun Controls(settings: Settings, engine: TimerEngine, accent: Color, onCustom: () -> Unit) {
    val m = settings.mode
    val busy = engine.isBusy
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .alpha(if (busy) 0.35f else 1f),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (m == Mode.TRAINING) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                StepButton("−", settings.t("minus"), enabled = !busy) { settings.rounds = settings.rounds - 1 }
                Column(Modifier.widthIn(min = 92.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    FightText("${settings.rounds}", 44.dp, settings)
                    Text(
                        settings.t("rounds").uppercase(),
                        style = ui(13f).copy(color = Muted, letterSpacing = 0.18.em),
                    )
                }
                StepButton("+", settings.t("plus"), enabled = !busy) { settings.rounds = settings.rounds + 1 }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            m.presets.forEach { s ->
                val on = !settings.isCustom(m) && settings.duration(m) == s
                Chip(on = on, accent = accent, modifier = Modifier.weight(1f), enabled = !busy,
                    onClick = { settings.pickPreset(s, m) }) {
                    Text(TimeFormat.short(s), style = ui(22f))
                }
            }
            val customOn = settings.isCustom(m)
            Chip(on = customOn, accent = accent, modifier = Modifier.weight(1f), enabled = !busy, dashed = true,
                onClick = onCustom) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        settings.t("custom").uppercase(),
                        style = ui(11f).copy(color = if (customOn) accent else Muted, letterSpacing = 0.12.em),
                    )
                    Text(TimeFormat.mss(settings.custom(m)), style = ui(19f))
                }
            }
        }
    }
}

// Actions: reset + start

@Composable
private fun Actions(settings: Settings, engine: TimerEngine, accent: Color) {
    val label = settings.t(
        when (engine.state) {
            TimerEngine.State.IDLE -> "start"
            TimerEngine.State.RUNNING -> "pause"
            TimerEngine.State.PAUSED -> "resume"
            TimerEngine.State.DONE -> "again"
        }
    )
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(68.dp)
                .clip(shape)
                .background(Panel)
                .border(2.dp, Line, shape)
                .clickable { engine.reset() }
                .semantics { contentDescription = settings.t("reset") },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = null, tint = TextColor, modifier = Modifier.size(30.dp))
        }
        GoButton(label, accent, settings, Modifier.weight(1f)) { engine.toggle() }
    }
}

@Composable
private fun GoButton(label: String, color: Color, settings: Settings, modifier: Modifier, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shape = RoundedCornerShape(18.dp)
    Box(modifier.height(74.dp)) {
        // Solid bottom edge, arcade style.
        Box(
            Modifier
                .fillMaxWidth()
                .height(68.dp)
                .offset(y = 6.dp)
                .clip(shape)
                .background(lerp(color, Color.Black, 0.45f))
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(68.dp)
                .offset(y = if (pressed) 3.dp else 0.dp)
                .clip(shape)
                .background(color)
                .clickable(interactionSource = interaction, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                label,
                style = TextStyle(
                    fontFamily = settings.font.family,
                    fontSize = dpSp(34f),
                    color = Ink,
                    letterSpacing = 0.06.em,
                ),
                maxLines = 1,
                modifier = Modifier.skew(settings.font.skewDeg),
            )
        }
    }
}

// Mode tabs

@Composable
private fun TabBar(settings: Settings, engine: TimerEngine) {
    val busy = engine.isBusy
    val fr = settings.lang == Lang.FR
    Row(
        Modifier
            .fillMaxWidth()
            .background(TabBarColor)
            .drawBehind { drawLine(Line, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
            .navigationBarsPadding()
            .padding(horizontal = 10.dp)
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Mode.entries.forEach { m ->
            val selected = settings.mode == m
            Column(
                Modifier
                    .weight(1f)
                    .height(62.dp)
                    .alpha(if (busy && !selected) 0.35f else 1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) m.color.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable(enabled = !busy) { settings.mode = m },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    Modifier
                        .width(if (selected) 36.dp else 26.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(m.color.copy(alpha = if (selected) 1f else 0.45f))
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    settings.tabLabel(m).uppercase(),
                    style = ui(if (fr) 12f else 14f).copy(color = m.color, letterSpacing = if (fr) 0.01.em else 0.06.em),
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}
