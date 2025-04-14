package org.timer.main.timer


expect class AlarmPlayer(context: Any? = null) {
    fun play(onEnded: () -> Unit = {})
    fun cancel()
    val context: Any?
}