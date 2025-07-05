package org.timer.main.settings

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.timer.main.domain.auth.*
import org.timer.main.domain.settings.*
import org.timer.main.domain.timer.*

class SettingsViewModel(
    private val settingsGateway: SettingsGateway,
    private val playAlarmUseCase: PlayAlarmUseCase,
    private val cancelAlarmUseCase: CancelAlarmUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _viewState = MutableStateFlow(SettingsViewState())
    val viewState: StateFlow<SettingsViewState> = _viewState.asStateFlow()

    init {
        settingsGateway.getAlarmSound()
            .onEach { alarmSound ->
                _viewState.update {
                    it.copy(selectedAlarmPos = alarmSound.ordinal)
                }
            }.launchIn(viewModelScope)
    }

    fun updatePomodoroMinutes(focusedMinutes: String) {
        _viewState.update {
            it.copy(
                pomodoroMinutes = focusedMinutes,
                showPomodoroError = focusedMinutes.isEmpty(),
            )
        }
        val timeSettings = getTimeSettingsFromTextFields()
        settingsGateway.setTimeSettings(timeSettings)
    }

    fun updateShortBreakMinutes(shortBreakMinutes: String) {
        _viewState.update {
            it.copy(
                shortBreakMinutes = shortBreakMinutes,
                showShortBreakError = shortBreakMinutes.isEmpty(),
            )
        }
        val timeSettings = getTimeSettingsFromTextFields()
        settingsGateway.setTimeSettings(timeSettings)
    }

    fun updateLongBreakMinutes(longBreakMinutes: String) {
        _viewState.update {
            it.copy(
                longBreakMinutes = longBreakMinutes,
                showLongBreakError = longBreakMinutes.isEmpty(),
            )
        }
        val timeSettings = getTimeSettingsFromTextFields()
        settingsGateway.setTimeSettings(timeSettings)
    }

    fun onPresetClick(position: Int) {
        _viewState.update { it.copy(selectedPresetPosition = position) }
        if (position == 0 || position == 1) {
            _viewState.update {
                it.copy(
                    shortBreakMinutes = "",
                    longBreakMinutes = "",
                    pomodoroMinutes = "",
                    showPomodoroError = false,
                    showShortBreakError = false,
                    showLongBreakError = false,
                )
            }
        }
        val timeSettings = when (position) {
            0 -> TimeSettings(
                25000L * 60,
                5000L * 60,
                15000L * 60
            )

            1 -> TimeSettings(
                50000L * 60,
                10000L * 60,
                30000L * 60
            )

            2 -> {
                getTimeSettingsFromTextFields()
            }

            else -> throw IllegalStateException()
        }

        settingsGateway.setTimeSettings(timeSettings)
    }

    private fun getTimeSettingsFromTextFields(): TimeSettings {
        val pomodoroMinutesText = _viewState.value.pomodoroMinutes
        val shortBreakMinutesText = _viewState.value.shortBreakMinutes
        val longBreakMinutesText = _viewState.value.longBreakMinutes

        return TimeSettings(
            if (pomodoroMinutesText.isBlank()) 0 else pomodoroMinutesText.toLong() * 1000 * 60,
            if (shortBreakMinutesText.isBlank()) 0 else shortBreakMinutesText.toLong() * 1000 * 60,
            if (longBreakMinutesText.isBlank()) 0 else longBreakMinutesText.toLong() * 1000 * 60
        )
    }

    fun onAlarmSoundClick(position: Int) {
        settingsGateway.setAlarmSound(AlarmSound.entries[position])
    }

    fun onPlayAlarmSoundPreviewClick() = viewModelScope.launch {
        cancelAlarmUseCase.invoke()
        playAlarmUseCase.invoke()
    }

    fun onSignOutClick() = viewModelScope.launch {
        logoutUseCase()
    }

}