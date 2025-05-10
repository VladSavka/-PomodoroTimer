package org.timer.main.timer


expect class AlarmPlayer(context: Any? = null) {
    fun play(uri: String, onEnded: () -> Unit = {})
    fun cancel()
    val context: Any?
    suspend fun feckePlay()
}