package org.timer.main.timer

import kotlinx.cinterop.*
import platform.Foundation.*

actual class CountDownTimer actual constructor(
    actual var totalMillis: Long,
    actual val onTick: (Long) -> Unit,
    actual val onFinish: () -> Unit,
    actual val isRunning: (Boolean) -> Unit
) {
    actual var currentMillis: Long = totalMillis
    private var targetTimeMillis: Long = 0L

    private var isTimerRunning = false

    private var iosTimer: NSTimer? = null

    actual fun startTimer() {
        if (isTimerRunning) return // Prevent starting if already running
        isTimerRunning = true
        isRunning(true)
//        countDownTimer =
//            object : android.os.CountDownTimer(currentMillis, 1000) { // Tick every 1 second
//                override fun onTick(millisUntilFinished: Long) {
//                    currentMillis = millisUntilFinished
//                    this@CountDownTimer.onTick(millisUntilFinished)
//                }
//
//                override fun onFinish() {
//                    isTimerRunning = false
//                    isRunning(false)
//                    currentMillis = 0
//                    this@CountDownTimer.onFinish()
//                }
//            }.start()
        // Calculate the target time in milliseconds (future time)
        val timeInFuture = totalMillis
        targetTimeMillis = DateTime.getCurrentTimeInMilliSeconds() + timeInFuture

        // Schedule the timer with the given interval
        iosTimer = NSTimer.scheduledTimerWithTimeInterval(
            interval = 1.0,
            repeats = true
        ) { _ ->
            onTimerTick(iosTimer!!)
        }
    }

    @ObjCAction
    fun onTimerTick(timer: NSTimer) {
        // Get the current time in milliseconds
        val currentTimeMillis = (NSDate().timeIntervalSince1970 * 1000).toLong()

        // Calculate the remaining time
        val millisUntilFinished = targetTimeMillis - currentTimeMillis

        if (millisUntilFinished <= 0) {
            // If the countdown is complete, call the finish method
            onTimerFinish(timer)
            return
        }

        // Calculate days, hours, minutes, and seconds
        val totalSeconds = millisUntilFinished / 1000
        val days = (totalSeconds / (24 * 60 * 60))
        val hours = ((totalSeconds % (24 * 60 * 60)) / (60 * 60))
        val minutes = ((totalSeconds % (60 * 60)) / 60)
        val seconds = (totalSeconds % 60)

        // Trigger the onTick callback with the correct time values
        onTick(days, hours, minutes, seconds)

        // Debugging print statement
        println("$days:$hours:$minutes:$seconds")
    }

    @ObjCAction
    fun onTimerFinish(timer: NSTimer) {
        onCountDownFinish()
        cancel() // Stop the timer
    }

    actual fun pauseTimer() {
        iosTimer?.invalidate()
        isTimerRunning = false
        isRunning(false)
    }

    actual fun resetTimer() {
        iosTimer?.invalidate()
        isTimerRunning = false
        isRunning(false)
        currentMillis = totalMillis
        onTick(totalMillis) // Update UI with initial value
    }

    actual fun isFinished(): Boolean {
        return currentMillis <= 0
    }
}