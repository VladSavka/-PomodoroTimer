package org.timer.main.timer

import androidx.lifecycle.*
import com.diamondedge.logging.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.*
import kotlinx.datetime.format.*
import org.timer.main.domain.settings.*
import org.timer.main.domain.timer.*
import org.timer.main.domain.video.*

private const val ITERATIONS_IN_ONE_CYCLE = 4

class TimerViewModel(
    private val settings: SettingsGateway,
    private val playAlarmUseCase: PlayAlarmUseCase,
    private val cancelAlarmUseCase: CancelAlarmUseCase
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

    private fun startShortBreak() {
        pomodoroTimer.resetTimer()
        longBreakTimer.resetTimer()
        shortBreakTimer.startTimer()
        _viewState.update {
            it.copy(
                selectedTabIndex = 1,
                timerState = TimerState.ShortBreak(
                    WorkoutVideosGateway.getWorkoutVideos().random(),
                    WorkoutVideosGateway.getDanceAudios().random()
                ),
                isShortBreakStarted = true,
            )
        }
    }

    private fun onShortBreakFinish() = viewModelScope.launch {
        playAlarmUseCase.invoke(onEnded = ::startNextPomodoroIteration)
    }

    private fun onLongBreakFinish() = viewModelScope.launch {
        playAlarmUseCase.invoke(onEnded = ::startNextPomodoroIteration)
    }


    private fun startNextPomodoroIteration() = viewModelScope.launch {
        pomodoroTimer.resetTimer()
        longBreakTimer.resetTimer()
        shortBreakTimer.resetTimer()
        pomodoroTimer.startTimer()

        _viewState.update {
            it.copy(
                selectedTabIndex = 0,
                timerState = TimerState.Pomodoro,
            )
        }
    }

    var timerJob: Job? = null

    private fun onPomodoroFinish() {
        log.debug { "onFinish" }
        incrementKittydoroNumber()
        timerJob = viewModelScope.launch {
            playAlarmUseCase.invoke(onEnded = {
                if (viewState.value.kittyDoroNumber % ITERATIONS_IN_ONE_CYCLE == 0) {
                    startLongBreak()
                } else {
                    startShortBreak()
                }
            })
        }
    }

    private fun incrementKittydoroNumber() {
        var kittyDoroNumber = viewState.value.kittyDoroNumber
        kittyDoroNumber++
        _viewState.update { it.copy(kittyDoroNumber = kittyDoroNumber) }
    }

    private fun startLongBreak() {
        shortBreakTimer.resetTimer()
        pomodoroTimer.resetTimer()
        longBreakTimer.startTimer()
        _viewState.update {
            it.copy(
                selectedTabIndex = 2,
                timerState = TimerState.LongBreak,
            )
        }
    }

    private fun onPomodoroTick(millis: Long) {
        log.debug { "onPomodoroTick " + millis.formatToMMSS() }
        _viewState.update { it.copy(pomodoroTime = millis.formatToMMSS()) }
    }

    private fun onShortBreakTick(millis: Long) {
        _viewState.update { it.copy(shortBreakTime = millis.formatToMMSS()) }
    }

    private fun onLongBreakTick(millis: Long) {
        _viewState.update { it.copy(longBreakTime = millis.formatToMMSS()) }
    }

    fun onPomodoroStartClick() {
        shortBreakTimer.resetTimer()
        longBreakTimer.resetTimer()
        pomodoroTimer.startTimer()
        if (_viewState.value.timerState !is TimerState.Pomodoro) {
            _viewState.update {
                it.copy(
                    timerState = TimerState.Pomodoro
                )
            }
        }
    }

    fun onShortBreakStartClick() {
        pomodoroTimer.resetTimer()
        longBreakTimer.resetTimer()
        shortBreakTimer.startTimer()
        if (_viewState.value.timerState !is TimerState.ShortBreak) {
            _viewState.update {
                it.copy(
                    timerState = TimerState.ShortBreak(
                        WorkoutVideosGateway.getWorkoutVideos().random(),
                        WorkoutVideosGateway.getDanceAudios().random()
                    )
                )
            }
        }
    }

    fun onLongBreakStartClick() {
        pomodoroTimer.resetTimer()
        shortBreakTimer.resetTimer()
        longBreakTimer.startTimer()
        if (_viewState.value.timerState !is TimerState.LongBreak) {
            _viewState.update {
                it.copy(
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
        initTimers()
        _viewState.update {
            it.copy(
                kittyDoroNumber = 0,
                timerState = TimerState.Pomodoro
            )
        }
    }

    fun onPomodoroPauseClick() {
        if (pomodoroTimer.isFinished()) {
            return
        }
        pomodoroTimer.pauseTimer()
    }

    fun onShortBreakPauseClick() {
        if (shortBreakTimer.isFinished()) {
            return
        }
        shortBreakTimer.pauseTimer()
    }

    fun onLongBreakPauseClick() {
        if (longBreakTimer.isFinished()) {
            return
        }
        longBreakTimer.pauseTimer()
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

    fun onPageChanged(currentPage: Int) {
        _viewState.update { it.copy(selectedTabIndex = currentPage) }
    }


    companion object {
        val log = logging()
    }
}