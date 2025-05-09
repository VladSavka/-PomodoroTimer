package org.timer.main.domain.timer

import org.timer.main.timer.*

class CancelAlarmUseCase(
    private val alarmPlayer: AlarmPlayer,
) {
     fun invoke() {
        alarmPlayer.cancel()
    }
}