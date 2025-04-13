package org.timer.main.timer

data class TimerViewState(
    val pomodoroTime: String = "00:00",
    val shortBreakTime: String = "00:00",
    val longBreakTime: String = "00:00",
    private val isPomodoroTimerRunning: Boolean = false,
    private val isShortBreakRunning: Boolean = false,
    private val isLongBreakTimerRunning: Boolean = false,
    val kittyDoroNumber: Int = 1,
    val videoLink: String? = null,
    val selectedTabIndex: Int = 0
) {
    val isPomodoroStartVisible: Boolean
        get() = !isPomodoroTimerRunning
    val isPomodoroPauseVisible: Boolean
        get() = isPomodoroTimerRunning

    val isShortBreakStartVisible: Boolean
        get() = !isShortBreakRunning
    val isShortBreakPauseVisible: Boolean
        get() = isShortBreakRunning

    val isLongBreakStartVisible: Boolean
        get() = !isLongBreakTimerRunning
    val isLongBreakPauseVisible: Boolean
        get() = isLongBreakTimerRunning

}
