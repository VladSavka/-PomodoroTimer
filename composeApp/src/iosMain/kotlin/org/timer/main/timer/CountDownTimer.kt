package org.timer.main.timer

actual class CountDownTimer actual constructor(
    totalMillis: Long,
    onTick: (Long) -> Unit,
    onFinish: () -> Unit
) {
    actual var millisInFuture: Long = 0
        get() = TODO("Not yet implemented")
    actual val onTick: (Long) -> Unit
        get() = TODO("Not yet implemented")
    actual val onFinish: () -> Unit
        get() = TODO("Not yet implemented")

    actual fun startTimer() {
    }

    actual fun pauseTimer() {
    }

    actual var currentMillis: Long = 0
}