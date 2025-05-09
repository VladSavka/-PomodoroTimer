package org.timer.main.domain.settings

import com.russhwolf.settings.*
import kotlinx.coroutines.flow.*

class SettingsGateway {
    private val settings: Settings = Settings()

    private val timeSettingsFlow =
        MutableStateFlow(TimeSettings(25000L * 60, 5000L * 60, 15000L * 60))

    private val alarmSoundFlow = MutableStateFlow(AlarmSound.entries[settings.getInt(KEY, 0)])


    fun getTimeSettings(): Flow<TimeSettings> = timeSettingsFlow.asStateFlow()

    fun setTimeSettings(timeSettings: TimeSettings) {
        timeSettingsFlow.value = timeSettings
    }

    fun setAlarmSound(alarmSound: AlarmSound) {
        alarmSoundFlow.value = alarmSound
        settings.putInt(KEY, alarmSound.ordinal)
    }

    fun getAlarmSound() = alarmSoundFlow.asStateFlow()

    private companion object {
        private const val KEY = "alarmSound"
    }
}