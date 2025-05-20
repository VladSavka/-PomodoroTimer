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

    fun showLiveActivity(totalTimeLeftMillis: Long)
}