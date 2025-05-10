package org.timer.main.domain.timer

import kotlinx.coroutines.flow.*
import org.timer.main.domain.settings.*
import org.timer.main.timer.*

class PlayAlarmUseCase(
    private val alarmPlayer: AlarmPlayer,
    private val settingsGateway: SettingsGateway
) {

    fun invoke(onEnded: () -> Unit = {}) {
        val alarmSound = settingsGateway.getAlarmSound().value
        alarmPlayer.play(alarmSound.toUri(), onEnded = onEnded)
    }
}