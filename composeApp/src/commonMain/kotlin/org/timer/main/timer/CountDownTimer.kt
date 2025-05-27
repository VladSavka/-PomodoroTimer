package org.timer.main.timer

expect class CountDownTimer(
    totalMillis: Long,
    onTick: (Long) -> Unit,
    onFinish: () -> Unit,
    isRunning: (Boolean) -> Unit,
) {
    var totalMillis: Long
    val onTick: (Long) -> Unit
    val onFinish: () -> Unit
    val isRunning: (Boolean) -> Unit
    var currentMillis: Long

    fun startTimer()
    fun pauseTimer()
    fun resetTimer()
    fun isFinished(): Boolean
    fun getCurrentTimeMillis(): Long
}

