package org.timer.main.timer

import kotlinx.cinterop.*
import platform.AVFAudio.*
import platform.AVFoundation.*
import platform.Foundation.*

actual class AlarmPlayer actual constructor(actual val context: Any?) {

    private var audioPlayer: AVPlayer? = null

    init {
        setUpAudioSession()
    }

    actual fun play(onEnded: () -> Unit) {
        val alarmSoundUrl: NSURL? =
            NSURL.URLWithString("https://upload.wikimedia.org/wikipedia/commons/transcoded/5/53/Felis_silvestris_catus_meows.ogg/Felis_silvestris_catus_meows.ogg.mp3?download")
        alarmSoundUrl?.let { url ->
            audioPlayer = AVPlayer(url)
            NSNotificationCenter.defaultCenter.addObserverForName(
                name = AVPlayerItemDidPlayToEndTimeNotification,
                `object` = audioPlayer?.currentItem,
                queue = NSOperationQueue.mainQueue,
                usingBlock = {
                    onEnded.invoke()
                }
            )
            audioPlayer?.play()
        }
    }

    actual fun cancel() {
        audioPlayer?.pause()
        audioPlayer = null
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setUpAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            println("Error setting up audio session: ${e.message}")
        }
    }
}