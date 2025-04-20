package org.timer.main.timer

data class TimerViewState(
    val pomodoroTime: String = "00:00",
    val shortBreakTime: String = "00:00",
    val longBreakTime: String = "00:00",
    private val isPomodoroTimerRunning: Boolean = false,
    private val isShortBreakTimerRunning: Boolean = false,
    private val isLongBreakTimerRunning: Boolean = false,
    val kittyDoroNumber: Int = 1,
    val timerState: TimerState = TimerState.Pomodoro,
    val selectedTabIndex: Int = 0,
    val isShortBreakStarted: Boolean = false,
    val isLongBreakStarted: Boolean = false,
) {
    val isPomodoroStartVisible: Boolean
        get() = !isPomodoroTimerRunning
    val isPomodoroPauseVisible: Boolean
        get() = isPomodoroTimerRunning

    val isShortBreakStartVisible: Boolean
        get() = !isShortBreakTimerRunning
    val isShortBreakPauseVisible: Boolean
        get() = isShortBreakTimerRunning

    val isLongBreakStartVisible: Boolean
        get() = !isLongBreakTimerRunning
    val isLongBreakPauseVisible: Boolean
        get() = isLongBreakTimerRunning
}

sealed class TimerState {
    data object Pomodoro : TimerState()
    data class ShortBreak(val videoLink: String, val audioLink: String) : TimerState()
    data object LongBreak : TimerState()
}
