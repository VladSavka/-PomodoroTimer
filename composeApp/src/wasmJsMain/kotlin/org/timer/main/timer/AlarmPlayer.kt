package org.timer.main.timer

import org.w3c.dom.Audio

actual class AlarmPlayer actual constructor() {
    private val audio =
        Audio("https://upload.wikimedia.org/wikipedia/commons/transcoded/5/53/Felis_silvestris_catus_meows.ogg/Felis_silvestris_catus_meows.ogg.mp3?download")


    actual fun play(onEnded: () -> Unit) {
        audio.play()
        audio.onended = {
            audio.removeEventListener("ended", audio.onended)
            audio.currentTime = 0.0
            onEnded.invoke()
        }
    }

    actual fun cancel() {
        audio.pause()
        audio.currentTime = 0.0
    }
}