package org.timer.main.timer

import android.os.Handler
import android.os.Looper
import android.os.SystemClock

actual class CountDownTimer actual constructor(
    actual var totalMillis: Long,
    actual val onTick: (Long) -> Unit,
    actual val onFinish: () -> Unit,
    actual val isRunning: (Boolean) -> Unit
) {
    actual var currentMillis: Long = totalMillis

    private var targetTimeMillis: Long = 0L
    private var isHandlerTicking = false

    private val handler = Handler(Looper.getMainLooper())

    private val tickRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!isHandlerTicking) {
                return
            }

            val millisLeft = targetTimeMillis - SystemClock.elapsedRealtime()

            if (millisLeft > 0) {
                currentMillis = millisLeft
                onTick(currentMillis)
                handler.postDelayed(this, 1000L)
            } else {
                currentMillis = 0
                onTick(0)
                isHandlerTicking = false
                isRunning(false)
                this@CountDownTimer.onFinish()
            }
        }
    }

    actual fun startTimer() {
        if (currentMillis <= 0) {
            currentMillis = totalMillis
        }

        targetTimeMillis = SystemClock.elapsedRealtime() + currentMillis

        if (!isHandlerTicking) {
            isHandlerTicking = true
            isRunning(true)
            handler.removeCallbacks(tickRunnable)
            handler.post(tickRunnable)
        } else {
            onTick(currentMillis)
            isRunning(true)
        }
    }

    actual fun pauseTimer() {
        if (!isHandlerTicking) return

        isHandlerTicking = false
        isRunning(false)
        handler.removeCallbacks(tickRunnable)

        val millisLeft = targetTimeMillis - SystemClock.elapsedRealtime()
        currentMillis = if (millisLeft > 0) millisLeft else 0
        onTick(currentMillis)
    }

    actual fun resetTimer() {
        isHandlerTicking = false
        isRunning(false)
        handler.removeCallbacks(tickRunnable)

        currentMillis = totalMillis
        targetTimeMillis = 0L
        onTick(totalMillis)
    }

    actual fun isFinished(): Boolean {
        if (targetTimeMillis > 0 && !isHandlerTicking) {
            return (targetTimeMillis - SystemClock.elapsedRealtime()) <= 0
        }
        return currentMillis <= 0
    }

    actual fun getCurrentTimeMillis(): Long {
        if (isHandlerTicking && targetTimeMillis > 0) {
            val millisLeft = targetTimeMillis - SystemClock.elapsedRealtime()
            currentMillis = if (millisLeft > 0) millisLeft else 0
            return currentMillis
        }
        return currentMillis
    }
}