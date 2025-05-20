package org.timer.main.timer

import org.timer.main.domain.settings.*

actual class MobileAlarm actual constructor(actual val context: Any?) {
    actual fun schedule(
        scheduledDateTimeMillis: Long,
        alarmSound: AlarmSound
    ) {
        //no-op
    }

    actual fun cancel() {
        //no-op
    }
}