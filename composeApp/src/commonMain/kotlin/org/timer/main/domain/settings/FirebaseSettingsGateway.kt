package org.timer.main.domain.settings

import kotlinx.coroutines.flow.*

expect class FirebaseSettingsGateway()  : SettingsGateway {
    override fun getTimeSettings(): Flow<TimeSettings>

    override suspend fun setTimeSettings(timeSettings: TimeSettings)

    override suspend fun setAlarmSound(alarmSound: AlarmSound)

    override fun getAlarmSound(): Flow<AlarmSound>
}