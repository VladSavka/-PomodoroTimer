package org.timer.main.timer

import org.timer.main.domain.settings.*

actual class MobileAlarm actual constructor(actual val context: Any?) {

    actual fun cancel() {
        //no-op
    }

    actual fun schedule(
        scheduledDateTimeMillis: Long,
        alarmSound: AlarmSound,
        title: String,
        body: String
    ) {
        //no-op
    }

    actual fun startLiveNotification(totalTimeLeftMillis: Long) {
    }
}