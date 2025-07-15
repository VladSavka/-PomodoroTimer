package org.timer.main.domain.settings

import kotlinx.coroutines.flow.*

actual class FirebaseSettingsGateway : SettingsGateway {
    actual override fun getTimeSettings(): Flow<TimeSettings> {
      return emptyFlow()
    }

    actual override suspend fun setTimeSettings(timeSettings: TimeSettings) {
    }

    actual override suspend fun setAlarmSound(alarmSound: AlarmSound) {
    }

    actual override fun getAlarmSound(): Flow<AlarmSound> {
       return emptyFlow()
    }

}