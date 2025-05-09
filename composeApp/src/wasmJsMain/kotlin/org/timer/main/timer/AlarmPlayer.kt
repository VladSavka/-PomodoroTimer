package org.timer.main.timer

import org.jetbrains.compose.resources.*
import org.w3c.dom.*
import pomodorotimer.composeapp.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
actual class AlarmPlayer actual constructor(actual val context: Any?) {
    private val audio: Audio


    init {
        val url = Res.getUri("files/Felis_silvestris_catus_meows.ogg.mp3")
        audio = Audio(url)
    }

    actual fun play(onEnded: () -> Unit) {
        try {
            audio.play()
            audio.onended = {
                audio.removeEventListener("ended", audio.onended)
                audio.currentTime = 0.0
                onEnded.invoke()
            }

        } catch (e: Exception) {
            print("Error playing audio: " + e.message)
            onEnded.invoke()
        }

    }

    actual fun cancel() {
        audio.pause()
        audio.currentTime = 0.0
    }
}