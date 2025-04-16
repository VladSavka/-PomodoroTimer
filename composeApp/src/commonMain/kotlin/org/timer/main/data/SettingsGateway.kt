package org.timer.main.data

import kotlinx.coroutines.flow.*
import org.timer.main.data.entity.*

object SettingsGateway {
    private val flow = MutableStateFlow(TimeSettings(25000L * 60, 5000L * 60, 15000L * 60))
    //private val flow = MutableStateFlow(TimeSettings(5_000L , 5_000, 10_000))

    fun getTimeSettings(): Flow<TimeSettings> = flow.asStateFlow()

    fun setTimeSettings(timeSettings: TimeSettings) {
        flow.value = timeSettings
    }
}