package io.github.jpx2882.ringtimer

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundPlayer(context: Context) {
    /**
     * GO = round starts, STOP = round ends (rest), END = session over,
     * TICK = 3-2-1 countdown, WARN = 10 seconds left.
     */
    enum class Event(val key: String) { GO("go"), STOP("stop"), END("end"), TICK("tick"), WARN("warn") }

    // SoundPool does not take audio focus, so your music keeps playing underneath.
    private val pool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val ids = HashMap<String, Int>()

    init {
        loadAll(context)
    }

    @SuppressLint("DiscouragedApi")
    private fun loadAll(context: Context) {
        for (pack in SoundPack.entries) {
            for (ev in Event.entries) {
                val name = "${pack.key}_${ev.key}"
                val res = context.resources.getIdentifier(name, "raw", context.packageName)
                if (res != 0) ids[name] = pool.load(context, res, 1)
            }
        }
    }

    fun play(ev: Event, pack: SoundPack) {
        val id = ids["${pack.key}_${ev.key}"] ?: return
        pool.play(id, 1f, 1f, 1, 0, 1f)
    }
}
