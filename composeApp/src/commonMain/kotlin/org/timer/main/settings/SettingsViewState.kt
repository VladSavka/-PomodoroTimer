package org.timer.main.settings

data class SettingsViewState(
    val selectedPresetPosition: Int = 0,
    val pomodoroMinutes: String = "",
    val shortBreakMinutes: String = "",
    val longBreakMinutes: String = "",
    val showPomodoroError: Boolean = false,
    val showShortBreakError: Boolean = false,
    val showLongBreakError: Boolean = false,
    val isConfirmEnabled: Boolean = true,
)