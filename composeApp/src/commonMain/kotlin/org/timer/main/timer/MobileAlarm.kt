package org.timer.main.timer

import org.timer.main.domain.settings.*

expect class MobileAlarm(context: Any? = null) {
    val context: Any?
    fun cancel()
    fun schedule(
        scheduledDateTimeMillis: Long,
        alarmSound: AlarmSound,
        title: String,
        body: String
    )

    fun startLiveNotification(
        title: String,
        isBreak: Boolean,
        totalTimeLeftMillis: Long,
        alarmSound: AlarmSound
    )

    fun stopLiveNotification()
}

lateinit var startLiveActivity: (title: String, isBreak: Boolean, totalTimeLeftMillis: Long, soundFileName: String) -> Unit

lateinit var cancelLiveActivity: () -> Unit


