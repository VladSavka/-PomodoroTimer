package org.timer.main.timer

import org.timer.main.domain.settings.*


expect class AlarmPlayer(context: Any? = null) {
    fun play(alarmSound: AlarmSound, onEnded: () -> Unit = {})
    fun cancel()
    val context: Any?
}