package org.timer.main.timer

import org.timer.main.domain.settings.*

actual class MobileAlarm actual constructor(actual val context: Any?) {
    actual fun cancel() {}

    actual fun schedule(
        scheduledDateTimeMillis: Long,
        alarmSound: AlarmSound,
        title: String,
        body: String
    ) {}

    actual fun stopLiveNotification() {}
    actual fun startLiveNotification(
        title: String,
        isBreak: Boolean,
        totalTimeLeftMillis: Long,
        alarmSound: AlarmSound
    ) {
    }

}