package org.timer.main.timer

actual class CountDownTimer actual constructor(
    totalMillis: Long,
    actual val onTick: (Long) -> Unit,
    actual val onFinish: () -> Unit,
    actual val isRunning: (Boolean) -> Unit
) {
    actual var totalMillis: Long
        get() = 1
        set(value) {}

    actual var currentMillis: Long
        get() = 1
        set(value) {}

    actual fun startTimer() {
    }

    actual fun pauseTimer() {
    }

    actual fun resetTimer() {
    }

    actual fun isFinished(): Boolean {
        TODO("Not yet implemented")
    }

}