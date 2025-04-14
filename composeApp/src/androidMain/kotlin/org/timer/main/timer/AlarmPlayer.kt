package org.timer.main.timer

import android.content.*
import android.media.*
import android.net.*

actual class AlarmPlayer actual constructor(actual val context: Any?) {

    private var mediaPlayer: MediaPlayer? = null
    private val alarmSoundUri: Uri =
        Uri.parse("android.resource://org.timer.main/raw/alarm_sound") // Replace with your actual resource ID

    actual fun play(onEnded: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(context as Context, alarmSoundUri)
                prepare()
                setOnCompletionListener {
                    onEnded()
                    release() // Release resources after playback
                    mediaPlayer = null
                }
                start()
            } catch (e: Exception) {
                // Handle exceptions, e.g., log the error
                e.printStackTrace()
                onEnded() // Call onEnded even if playback fails
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