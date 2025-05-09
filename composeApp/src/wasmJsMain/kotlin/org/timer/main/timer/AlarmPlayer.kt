package org.timer.main.timer

import com.diamondedge.logging.*
import org.jetbrains.compose.resources.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import pomodorotimer.composeapp.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
actual class AlarmPlayer actual constructor(actual val context: Any?) {
    private var audio: Audio? = null

    actual fun play(uri: String, onEnded: () -> Unit) {
        try {
            audio = Audio(uri).apply {
                // Add an error listener
                onerror = { event: JsAny?, message: String, lineno: Int, colno: Int, error: JsAny? ->
                    // Log the parameters provided by the binding
                    log.e { "Audio playback error: message=$message, lineno=$lineno, colno=$colno" }
                    if (error != null) {
                         val mediaError = error as? MediaError
                         if (mediaError != null) {
                             log.e { "Error code: ${mediaError.code}" }
                         } else {
                             log.e { "Raw error object: $error" }
                         }
                        log.e { "Raw event object: $event" } // Log the raw object for inspection
                    }

                    // Clean up and invoke onEnded even on error
                    cleanupAudio()
                    log.e { "Audio playback error: onEnded.invoke() " + uri}
                    onEnded.invoke()
                    null
                }

                onended = {
                    // Clean up and invoke onEnded when playback finishes
                    cleanupAudio()
                    log.e { "Audio playback sucsses: onEnded.invoke() " + uri}
                    onEnded.invoke()
                }
                log.d {"start playing audio: $uri"}
                play()
            }
        } catch (e: Exception) {
            log.e(e) { "Exception during audio playback setup:  ${e.message}"}
            // Ensure onEnded is called even if setup fails
            onEnded.invoke()
        }
    }

    actual fun cancel() {
        cleanupAudio()
    }

    private fun cleanupAudio() {
        log.d {"cleanupAudio"}
        audio?.apply {
            pause()
            // Remove listeners to prevent memory leaks and unintended behavior
            onended = null
            onerror = null
            // Reset current time (optional, but good practice)
            currentTime = 0.0
        }
        audio = null
    }

    companion object {
        val log = logging("AlarmPlayer")
    }
}