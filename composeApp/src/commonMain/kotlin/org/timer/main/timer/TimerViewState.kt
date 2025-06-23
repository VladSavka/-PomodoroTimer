package org.timer.main.timer

import org.timer.main.domain.video.*

data class TimerViewState(
    val pomodoroTime: String = "00:00",
    val shortBreakTime: String = "00:00",
    val longBreakTime: String = "00:00",
    val isPomodoroTimerRunning: Boolean = false,
    val isShortBreakTimerRunning: Boolean = false,
    val isLongBreakTimerRunning: Boolean = false,
    val kittyDoroNumber: Int = 0,
    val timerState: TimerState = TimerState.Pomodoro(
        WorkoutVideosGateway.getWorkoutVideos().random().url,
        WorkoutVideosGateway.getDanceAudios().random()
    ),
    val selectedTabIndex: Int = 0,
    val navigateToShortBreakActivity: Boolean = false,
    val navigateToLongBreakActivity: Boolean = false,
    val navigateToActivitiesScreen: Boolean = false,
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
    data class Pomodoro(val videoLink: String, val audioLink: String) : TimerState()
    data class ShortBreak(val videoLink: String, val audioLink: String) : TimerState()
    data object LongBreak : TimerState()
}
