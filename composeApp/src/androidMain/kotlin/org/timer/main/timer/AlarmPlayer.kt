package org.timer.main.timer

import android.content.*
import android.media.*
import android.net.*
import android.os.*
import org.timer.main.domain.settings.*

actual class AlarmPlayer actual constructor(actual val context: Any?) {
    private var mediaPlayer: MediaPlayer? = null

    actual fun cancel() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
    actual fun play(alarmSound: AlarmSound, onEnded: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            try {
                setWakeMode(context as Context, PowerManager.PARTIAL_WAKE_LOCK)
                setDataSource(context,  Uri.parse(alarmSound.toUri()))
                prepare()
                setOnCompletionListener {
                    onEnded()
                    release()
                    mediaPlayer = null
                }
                start()
            } catch (e: Exception) {
                e.printStackTrace()
                onEnded()
                release()
                mediaPlayer = null
            }
        }
    }
}