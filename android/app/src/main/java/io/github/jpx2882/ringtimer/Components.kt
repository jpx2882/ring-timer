package io.github.jpx2882.ringtimer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.tan

/** Converts a size in dp to sp, ignoring the system font scale (the layout is fixed). */
@Composable
fun dpSp(size: Float): TextUnit = with(LocalDensity.current) { size.dp.toSp() }

/** UI text style (Barlow Condensed). */
@Composable
fun ui(size: Float, heavy: Boolean = true): TextStyle = TextStyle(
    fontFamily = Barlow,
    fontWeight = if (heavy) FontWeight.ExtraBold else FontWeight.SemiBold,
    fontSize = dpSp(size),
)

/** Slants content like a fighting-game logo, around its vertical center. */
fun Modifier.skew(deg: Float): Modifier =
    if (deg == 0f) this
    else drawWithContent {
        val c = tan(Math.toRadians(deg.toDouble())).toFloat()
        val m = Matrix()
        m.values[Matrix.SkewX] = c
        m.values[Matrix.TranslateX] = -c * size.height / 2f
        withTransform({ transform(m) }) {
            this@drawWithContent.drawContent()
        }
    }

fun Modifier.dashedBorder(color: Color, radius: Dp): Modifier = drawWithContent {
    drawContent()
    val s = 2.dp.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(s / 2, s / 2),
        size = Size(size.width - s, size.height - s),
        cornerRadius = CornerRadius(radius.toPx()),
        style = Stroke(width = s, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx()))),
    )
}

/** Fighting-game title text: gradient fill, thick dark outline, hard drop shadow, slight slant. */
@Composable
fun FightText(
    text: String,
    size: Dp,
    settings: Settings,
    modifier: Modifier = Modifier,
    fixedDigits: Boolean = false,
    font: TitleFont = settings.font,
) {
    val pal = settings.palette
    val density = LocalDensity.current
    val fontSize = with(density) { size.toSp() }
    val strokePx = with(density) { (size * 0.07f).coerceAtLeast(3.dp).toPx() }
    val shadowPx = with(density) { (size * 0.05f).toPx() }

    val base = TextStyle(fontFamily = font.family, fontSize = fontSize)
    val outline = base.copy(
        color = pal.stroke,
        drawStyle = Stroke(width = strokePx, join = StrokeJoin.Round),
        shadow = Shadow(Color.Black.copy(alpha = 0.6f), Offset(0f, shadowPx), 0f),
    )
    val fill = base.copy(brush = pal.brush)

    Box(modifier.skew(font.skewDeg), contentAlignment = Alignment.Center) {
        Glyphs(text, outline, fixedDigits, size, font)
        Glyphs(text, fill, fixedDigits, size, font)
    }
}

@Composable
private fun Glyphs(text: String, style: TextStyle, fixedDigits: Boolean, size: Dp, font: TitleFont) {
    if (fixedDigits) {
        // Same width for every digit so the clock does not wobble.
        Row(verticalAlignment = Alignment.CenterVertically) {
            text.forEach { ch ->
                Box(
                    Modifier.width(size * (if (ch == ':') font.colonWidth else font.digitWidth)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(ch.toString(), style = style, maxLines = 1, softWrap = false)
                }
            }
        }
    } else {
        Text(text, style = style, maxLines = 1, softWrap = false)
    }
}

/** Preset / custom time chip. */
@Composable
fun Chip(
    on: Boolean,
    accent: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dashed: Boolean = false,
    height: Dp = 50.dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor = if (on) accent else Line
    Box(
        modifier
            .height(height)
            .clip(shape)
            .background(Panel)
            .background(if (on) accent.copy(alpha = 0.22f) else Color.Transparent)
            .then(if (dashed && !on) Modifier.dashedBorder(borderColor, 12.dp) else Modifier.border(2.dp, borderColor, shape))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        CompositionLocalProvider(LocalContentColor provides if (on) Color.White else TextColor) {
            content()
        }
    }
}

/** Round − / + button. */
@Composable
fun StepButton(symbol: String, label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Panel)
            .border(2.dp, Line, CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, style = ui(30f), color = TextColor)
    }
}

/** Round − / + button that repeats while held down. */
@Composable
fun RepeatButton(symbol: String, onStep: () -> Unit) {
    val scope = rememberCoroutineScope()
    val step by rememberUpdatedState(onStep)
    Box(
        Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Panel)
            .border(2.dp, Line, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    step()
                    val job = scope.launch {
                        delay(400)
                        while (true) {
                            step()
                            delay(90)
                        }
                    }
                    tryAwaitRelease()
                    job.cancel()
                })
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, style = ui(30f), color = TextColor)
    }
}
