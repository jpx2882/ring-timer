package io.github.jpx2882.ringtimer

import android.app.Application
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/** Holds the timer so it survives the activity being recreated. */
class RingTimerApplication : Application() {
    val settings by lazy { Settings(this) }
    val engine by lazy { TimerEngine(settings, SoundPlayer(this)) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        // Volume buttons control the timer sounds.
        volumeControlStream = AudioManager.STREAM_MUSIC

        val app = application as RingTimerApplication
        val engine = app.engine
        engine.onAwakeChange = { on ->
            if (on) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        if (engine.state == TimerEngine.State.RUNNING) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        setContent {
            RingTimerScreen(app.settings, engine)
        }
    }
}
