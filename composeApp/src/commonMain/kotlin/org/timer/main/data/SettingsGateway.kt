package org.timer.main.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsGateway {
    private val flow = MutableStateFlow(TimeSettings(25000L * 60, 5000L * 60, 15000L * 60))
    //private val flow = MutableStateFlow(TimeSettings(5_000L , 5_000, 10_000))

    fun getTimeSettings(): Flow<TimeSettings> = flow.asStateFlow()

    fun setTimeSettings(timeSettings: TimeSettings) {
        flow.value = timeSettings
    }
}