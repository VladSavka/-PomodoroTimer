package org.timer.main.timer

actual class CountDownTimer actual constructor(
    actual var totalMillis: Long,
    actual val onTick: (Long) -> Unit,
    actual val onFinish: () -> Unit,
    actual val isRunning: (Boolean) -> Unit
) {
    actual var currentMillis: Long = totalMillis

    private var countDownTimer: android.os.CountDownTimer? = null
    private var isTimerRunning = false


    actual fun startTimer() {
        if (isTimerRunning) return // Prevent starting if already running
        isTimerRunning = true
        isRunning(true)
        countDownTimer =
            object : android.os.CountDownTimer(currentMillis, 1000) { // Tick every 1 second
                override fun onTick(millisUntilFinished: Long) {
                    currentMillis = millisUntilFinished
                    this@CountDownTimer.onTick(millisUntilFinished)
                }

                override fun onFinish() {
                    isTimerRunning = false
                    isRunning(false)
                    currentMillis = 0
                    this@CountDownTimer.onFinish()
                }
            }.start()
    }

    actual fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isRunning(false)
    }

    actual fun resetTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isRunning(false)
        currentMillis = totalMillis
        onTick(totalMillis) // Update UI with initial value
    }

    actual fun isFinished(): Boolean {
        return currentMillis <= 0
    }

    actual fun getCurrentTimeMillis(): Long = currentMillis
}