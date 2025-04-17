package org.timer.main.settings

import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import org.timer.main.domain.settings.*

class SettingsViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(SettingsViewState())
    val viewState: StateFlow<SettingsViewState> = _viewState.asStateFlow()

    fun onPresetConfirmed(position: Int) {
        _viewState.update { it.copy(selectedPresetPosition = position) }
        when (position) {
            0 -> SettingsGateway.setTimeSettings(
                TimeSettings(
                    25000L * 60,
                    5000L * 60,
                    15000L * 60
                )
            )

            1 -> SettingsGateway.setTimeSettings(
                TimeSettings(
                    50000L * 60,
                    10000L * 60,
                    30000L * 60
                )
            )

            2 -> if (isFormValid()) {
                SettingsGateway.setTimeSettings(
                    TimeSettings(
                        _viewState.value.focusMinutes.toLong() * 1000 * 60,
                        _viewState.value.shortBreakMinutes.toLong() * 1000 * 60,
                        _viewState.value.longBreakMinutes.toLong() * 1000 * 60
                    )
                )
            }

            else -> throw IllegalStateException()
        }
        updateConfirmButtonVisibility()
    }


    fun updateFocusMinutes(focusedMinutes: String) {
        _viewState.update {
            it.copy(
                focusMinutes = focusedMinutes,
                showFocusError = focusedMinutes.isEmpty(),
            )
        }
        updateConfirmButtonVisibility()
    }

    fun updateShortBreakMinutes(shortBreakMinutes: String) {
        _viewState.update {
            it.copy(
                shortBreakMinutes = shortBreakMinutes,
                showShortBreakError = shortBreakMinutes.isEmpty(),
            )
        }
        updateConfirmButtonVisibility()

    }

    fun updateLongBreakMinutes(longBreakMinutes: String) {
        _viewState.update {
            it.copy(
                longBreakMinutes = longBreakMinutes,
                showLongBreakError = longBreakMinutes.isEmpty(),
            )
        }
        updateConfirmButtonVisibility()

    }

    fun onPresetSelected(position: Int) {
        _viewState.update { it.copy(selectedPresetPosition = position) }
        updateConfirmButtonVisibility()
    }

    private fun updateConfirmButtonVisibility() {
        if (viewState.value.selectedPresetPosition == 2) {
            _viewState.update { it.copy(isConfirmEnabled = isFormValid()) }
        } else {
            _viewState.update { it.copy(isConfirmEnabled = true) }
        }
    }

    private fun isFormValid() =
        _viewState.value.focusMinutes.isNotEmpty()
                && _viewState.value.shortBreakMinutes.isNotEmpty()
                && _viewState.value.longBreakMinutes.isNotEmpty()
}