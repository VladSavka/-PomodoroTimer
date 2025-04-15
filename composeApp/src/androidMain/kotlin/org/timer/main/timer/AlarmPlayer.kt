package org.timer.main.timer

import android.content.*
import android.media.*
import android.net.*
import android.net.wifi.*
import android.os.*

actual class AlarmPlayer actual constructor(actual val context: Any?) {
    private val context1: Context = context as Context
    private var mediaPlayer: MediaPlayer? = null
    private val alarmSoundUri: Uri =
        Uri.parse("https://upload.wikimedia.org/wikipedia/commons/transcoded/5/53/Felis_silvestris_catus_meows.ogg/Felis_silvestris_catus_meows.ogg.mp3?download")
    actual fun play(onEnded: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            try {
                setWakeMode(context as Context, PowerManager.PARTIAL_WAKE_LOCK)
                setDataSource(context as Context, alarmSoundUri)
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

    actual fun cancel() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
}