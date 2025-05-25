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

    fun startLiveActivity(title: String, totalTimeLeftMillis: Long)

    fun pauseLiveActivity(totalTimeLeftMillis: Long)

    fun resumeLiveActivity()

    fun cancelLiveActivity()
}

lateinit var startLiveActivity: (title: String, totalTimeLeftMillis: Long) -> Unit

lateinit var pauseLiveActivity: (totalTimeLeftMillis: Long) -> Unit

lateinit var resumeLiveActivity: () -> Unit

lateinit var cancelLiveActivity: () -> Unit


