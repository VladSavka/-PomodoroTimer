package org.timer.main.settings

data class SettingsViewState(
    val selectedPresetPosition: Int = 0,
    val focusMinutes:String = "",
    val shortBreakMinutes:String = "",
    val longBreakMinutes:String = "",
    val showFocusError: Boolean= false,
    val showShortBreakError:Boolean= false,
    val showLongBreakError:Boolean = false,
    val isConfirmEnabled:Boolean = true,
    )