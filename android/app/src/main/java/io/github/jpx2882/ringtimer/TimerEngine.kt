package io.github.jpx2882.ringtimer

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.ceil
import kotlin.math.max

/** Runs the countdown. In Training it chains: round → rest → round … → end. */
class TimerEngine(private val settings: Settings, private val sound: SoundPlayer) {
    enum class State { IDLE, RUNNING, PAUSED, DONE }
    enum class Phase { READY, WORK, REST }

    var state by mutableStateOf(State.IDLE)
        private set
    var phase by mutableStateOf(Phase.READY)
        private set
    var round by mutableIntStateOf(1)
        private set
    var remaining by mutableDoubleStateOf(0.0)
        private set
    var total by mutableDoubleStateOf(1.0)
        private set
    /** Shows "FIGHT!" until this time (elapsedRealtime, ms). */
    var fightUntil by mutableLongStateOf(0L)
        private set

    /** Keeps the screen on while the timer runs (set by the activity). */
    var onAwakeChange: (Boolean) -> Unit = {}
    /** Small vibration on start and on each phase change (set by the UI). */
    var onBeat: () -> Unit = {}

    private var endAt = 0L
    private var lastSecond = -1

    private fun now() = SystemClock.elapsedRealtime()

    val isBusy: Boolean get() = state != State.IDLE

    /** 1 = full ring, 0 = empty. */
    val progress: Float
        get() = when (state) {
            State.IDLE -> 1f
            State.DONE -> 0f
            else -> if (phase == Phase.READY) 1f else (remaining / total).toFloat().coerceIn(0f, 1f)
        }

    fun isShowingFight(): Boolean = now() < fightUntil

    // Controls

    fun toggle() {
        when (state) {
            State.IDLE, State.DONE -> start()
            State.RUNNING -> pause()
            State.PAUSED -> resume()
        }
    }

    fun reset() {
        state = State.IDLE
        phase = Phase.READY
        round = 1
        onAwakeChange(false)
    }

    private fun start() {
        round = 1
        begin(Phase.READY, 3)
        state = State.RUNNING
        onAwakeChange(true)
        onBeat()
    }

    private fun pause() {
        remaining = max(0.0, (endAt - now()) / 1000.0)
        state = State.PAUSED
        onAwakeChange(false)
    }

    private fun resume() {
        endAt = now() + (remaining * 1000).toLong()
        lastSecond = -1
        state = State.RUNNING
        onAwakeChange(true)
    }

    // Ticking (called ~20 times per second by the UI)

    fun tick() {
        if (state != State.RUNNING) return
        remaining = max(0.0, (endAt - now()) / 1000.0)
        val sec = ceil(remaining).toInt()

        if (sec != lastSecond) {
            lastSecond = sec
            if (phase == Phase.READY) {
                if (sec > 0) play(SoundPlayer.Event.TICK)
            } else {
                if (sec == 10 && total > 20) play(SoundPlayer.Event.WARN)
                if (sec in 1..3) play(SoundPlayer.Event.TICK)
            }
        }

        if (remaining <= 0.0) advance()
    }

    private fun advance() {
        val mode = settings.mode
        when {
            phase == Phase.READY -> {
                play(SoundPlayer.Event.GO)
                fightUntil = now() + 900
                begin(Phase.WORK, settings.duration(mode))
            }
            phase == Phase.WORK && mode == Mode.TRAINING && round < settings.rounds -> {
                play(SoundPlayer.Event.STOP)
                begin(Phase.REST, settings.duration(Mode.REST))
            }
            phase == Phase.REST -> {
                round += 1
                play(SoundPlayer.Event.GO)
                begin(Phase.WORK, settings.duration(Mode.TRAINING))
            }
            else -> finish()
        }
        onBeat()
    }

    private fun finish() {
        play(SoundPlayer.Event.END)
        remaining = 0.0
        state = State.DONE
        onAwakeChange(false)
    }

    private fun begin(p: Phase, seconds: Int) {
        phase = p
        total = max(1, seconds).toDouble()
        remaining = total
        endAt = now() + (total * 1000).toLong()
        lastSecond = -1
    }

    private fun play(ev: SoundPlayer.Event) {
        if (settings.sound) sound.play(ev, settings.pack)
    }

    /** Lets the menu play a sample when a sound theme is picked. */
    fun preview(pack: SoundPack) = sound.play(SoundPlayer.Event.GO, pack)
}
