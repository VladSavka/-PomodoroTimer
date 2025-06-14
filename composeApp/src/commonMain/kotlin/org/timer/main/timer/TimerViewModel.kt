package org.timer.main.timer

import androidx.lifecycle.*
import com.diamondedge.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import kotlinx.datetime.format.*
import org.timer.main.*
import org.timer.main.domain.settings.*
import org.timer.main.domain.timer.*
import org.timer.main.domain.video.*

private const val ITERATIONS_IN_ONE_CYCLE = 4

class TimerViewModel(
    private val settings: SettingsGateway,
    private val playAlarmUseCase: PlayAlarmUseCase,
    private val cancelAlarmUseCase: CancelAlarmUseCase,
    private val mobileAlarm: MobileAlarm
) : ViewModel() {
    private val _viewState = MutableStateFlow(TimerViewState())
    val viewState: StateFlow<TimerViewState> = _viewState.asStateFlow()

    private lateinit var pomodoroTimer: CountDownTimer
    private lateinit var shortBreakTimer: CountDownTimer
    private lateinit var longBreakTimer: CountDownTimer


    init {
        viewModelScope.launch {
            initTimers()
        }

        settings.getTimeSettings()
            .onEach { resetTimers() }
            .launchIn(viewModelScope)
    }

    private suspend fun initTimers() {
        val settings = settings.getTimeSettings().first()
        pomodoroTimer = CountDownTimer(
            settings.pomodoroTime,
            onTick = ::onPomodoroTick,
            onFinish = ::onPomodoroFinish,
            isRunning = { isRunning -> _viewState.update { it.copy(isPomodoroTimerRunning = isRunning) } }

        )
        shortBreakTimer = CountDownTimer(
            settings.shortBreakTime,
            onTick = ::onShortBreakTick,
            onFinish = ::onShortBreakFinish,
            isRunning = { isRunning -> _viewState.update { it.copy(isShortBreakTimerRunning = isRunning) } }
        )
        longBreakTimer = CountDownTimer(
            settings.longBreakTime,
            onTick = ::onLongBreakTick,
            onFinish = ::onLongBreakFinish,
            isRunning = { isRunning -> _viewState.update { it.copy(isLongBreakTimerRunning = isRunning) } }
        )

        _viewState.update {
            it.copy(
                pomodoroTime = settings.pomodoroTime.formatToMMSS(),
                shortBreakTime = settings.shortBreakTime.formatToMMSS(),
                longBreakTime = settings.longBreakTime.formatToMMSS(),
            )
        }
    }

    private fun onShortBreakFinish() {
        if (isMobile()) {
            navigateToPomodoroTab()
        } else {
            playAlarmUseCase.invoke(onEnded = ::navigateToPomodoroTab)
        }
    }

    private fun onLongBreakFinish() {
        if (isMobile()) {
            navigateToPomodoroTab()
        } else {
            playAlarmUseCase.invoke(onEnded = ::navigateToPomodoroTab)
        }
    }


    private fun navigateToPomodoroTab() = viewModelScope.launch {
        pomodoroTimer.resetTimer()
        longBreakTimer.resetTimer()
        shortBreakTimer.resetTimer()

        _viewState.update {
            it.copy(
                selectedTabIndex = 0,
                timerState = TimerState.Pomodoro(
                    WorkoutVideosGateway.getWorkoutVideos().random(),
                    WorkoutVideosGateway.getDanceAudios().random()
                ),
            )
        }
    }

    private var timerJob: Job? = null

    private fun onPomodoroFinish() {
        incrementKittydoroNumber()
        if (isMobile()) {
            navigateToBreakTab()
            _viewState.update { it.copy(navigateToActivitiesScreen = true) }
        } else {
            playAlarmUseCase.invoke(onEnded = {
                navigateToBreakTab()
            })
        }

    }

    private fun navigateToBreakTab() {
        if (viewState.value.kittyDoroNumber % ITERATIONS_IN_ONE_CYCLE == 0) {
            navigateToLongBreakTab()
        } else {
            navigateToShortBreakTab()
        }
    }

    private fun navigateToShortBreakTab() {
        pomodoroTimer.resetTimer()
        longBreakTimer.resetTimer()
        shortBreakTimer.resetTimer()
        _viewState.update {
            it.copy(
                selectedTabIndex = 1,
                timerState = TimerState.ShortBreak(
                    WorkoutVideosGateway.getWorkoutVideos().random(),
                    WorkoutVideosGateway.getDanceAudios().random()
                ),
            )
        }
    }

    private fun incrementKittydoroNumber() {
        var kittyDoroNumber = viewState.value.kittyDoroNumber
        kittyDoroNumber++
        _viewState.update { it.copy(kittyDoroNumber = kittyDoroNumber) }
    }

    private fun navigateToLongBreakTab() {
        shortBreakTimer.resetTimer()
        pomodoroTimer.resetTimer()
        longBreakTimer.resetTimer()
        _viewState.update {
            it.copy(
                selectedTabIndex = 2,
                timerState = TimerState.LongBreak,
            )
        }
    }

    private fun onPomodoroTick(millis: Long) {
        _viewState.update { it.copy(pomodoroTime = millis.formatToMMSS()) }
    }

    private fun onShortBreakTick(millis: Long) {
        _viewState.update { it.copy(shortBreakTime = millis.formatToMMSS()) }
    }

    private fun onLongBreakTick(millis: Long) {
        _viewState.update { it.copy(longBreakTime = millis.formatToMMSS()) }
    }

    fun onPomodoroStartClick() = viewModelScope.launch {
        shortBreakTimer.resetTimer()
        longBreakTimer.resetTimer()
        if (!_viewState.value.isPomodoroTimerRunning) {
            mobileAlarm.startLiveNotification(
                "Kittidoro " + (viewState.value.kittyDoroNumber + 1),
                false,
                pomodoroTimer.getCurrentTimeMillis(),
                settings.getAlarmSound().value
            )

            scheduleAlarm(
                pomodoroTimer.getCurrentTimeMillis(),
                "Kittidoro Finished!",
                "Take a break Kitty"
            )
            pomodoroTimer.startTimer()
        }
        if (_viewState.value.timerState !is TimerState.Pomodoro) {
            _viewState.update {
                it.copy(
                    selectedTabIndex = 0,
                    timerState = TimerState.Pomodoro(
                        WorkoutVideosGateway.getWorkoutVideos().random(),
                        WorkoutVideosGateway.getDanceAudios().random()
                    )
                )
            }
        }
    }

    private fun scheduleAlarm(currentTimerMillis: Long, title: String, body: String) {
        mobileAlarm.cancel()
        val scheduleDate = Clock.System.now().toEpochMilliseconds() + currentTimerMillis
        val alarmSound = settings.getAlarmSound().value
        mobileAlarm.schedule(scheduleDate, alarmSound, title, body)
    }

    fun onShortBreakStartClick() = viewModelScope.launch {
        pomodoroTimer.resetTimer()
        longBreakTimer.resetTimer()
        if (!_viewState.value.isShortBreakTimerRunning) {
            mobileAlarm.startLiveNotification(
                "Short break",
                true,
                shortBreakTimer.getCurrentTimeMillis(),
                settings.getAlarmSound().value
            )
            scheduleAlarm(
                shortBreakTimer.getCurrentTimeMillis(),
                "Short break finished",
                "Keep the eye on the ball!"
            )
            shortBreakTimer.startTimer()
        }
        if (_viewState.value.timerState !is TimerState.ShortBreak) {
            _viewState.update {
                it.copy(
                    selectedTabIndex = 1,
                    timerState = TimerState.ShortBreak(
                        WorkoutVideosGateway.getWorkoutVideos().random(),
                        WorkoutVideosGateway.getDanceAudios().random()
                    )
                )
            }
        }
    }

    fun onLongBreakStartClick() = viewModelScope.launch {
        pomodoroTimer.resetTimer()
        shortBreakTimer.resetTimer()
        if (!_viewState.value.isLongBreakTimerRunning) {
            mobileAlarm.startLiveNotification(
                "Long break",
                true,
                longBreakTimer.getCurrentTimeMillis(),
                settings.getAlarmSound().value
            )

            scheduleAlarm(
                longBreakTimer.getCurrentTimeMillis(),
                "Long break finished",
                "Keep the eye on the ball!"
            )
            longBreakTimer.startTimer()
        }
        if (_viewState.value.timerState !is TimerState.LongBreak) {
            _viewState.update {
                it.copy(
                    selectedTabIndex = 2,
                    timerState = TimerState.LongBreak
                )
            }
        }
    }

    fun onResetClick() {
        resetTimers()
    }

    private fun resetTimers() = viewModelScope.launch {
        timerJob?.cancel()
        cancelAlarmUseCase.invoke()
        pomodoroTimer.pauseTimer()
        shortBreakTimer.pauseTimer()
        longBreakTimer.pauseTimer()
        mobileAlarm.cancel()
        mobileAlarm.stopLiveNotification()
        initTimers()
        _viewState.update {
            it.copy(
                kittyDoroNumber = 0,
                timerState = TimerState.Pomodoro(
                    WorkoutVideosGateway.getWorkoutVideos().random(),
                    WorkoutVideosGateway.getDanceAudios().random()
                )
            )
        }
    }

    fun onPomodoroPauseClick() {
        if (pomodoroTimer.isFinished()) {
            return
        }
        mobileAlarm.cancel()
        pomodoroTimer.pauseTimer()
        mobileAlarm.stopLiveNotification()
    }

    fun onShortBreakPauseClick() {
        if (shortBreakTimer.isFinished()) {
            return
        }
        mobileAlarm.cancel()
        shortBreakTimer.pauseTimer()
        mobileAlarm.stopLiveNotification()
    }

    fun onLongBreakPauseClick() {
        if (longBreakTimer.isFinished()) {
            return
        }
        mobileAlarm.cancel()
        longBreakTimer.pauseTimer()
        mobileAlarm.stopLiveNotification()
    }




    fun onPageChanged(currentPage: Int) {
        _viewState.update { it.copy(selectedTabIndex = currentPage) }
    }

    fun onNavigatedToActivitiesScreen() {
        _viewState.update { it.copy(navigateToActivitiesScreen = false) }
    }

    private fun Long.formatToMMSS(): String {
        val time = LocalTime.fromMillisecondOfDay(this.toInt())
        return if (time.hour >= 1) {
            time.format(LocalTime.Format {
                hour()
                char(':')
                minute()
                char(':')
                second()
            })
        } else {
            time.format(LocalTime.Format {
                minute()
                char(':')
                second()
            })
        }
    }

    companion object {
        val log = logging()
    }
}