package org.timer.main.domain.timer

import org.timer.main.domain.settings.*
import org.timer.main.timer.*

class PlayAlarmUseCase(
    private val alarmPlayer: AlarmPlayer,
    private val settingsGateway: PersistentSettingsGateway
) {

    fun invoke(onEnded: () -> Unit = {}) {
        val alarmSound = settingsGateway.getAlarmSound().value
        alarmPlayer.play(alarmSound, onEnded = onEnded)
    }
}