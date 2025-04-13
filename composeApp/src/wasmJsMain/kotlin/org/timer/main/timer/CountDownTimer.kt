package org.timer.main.timer

import kotlinx.browser.document
import kotlinx.datetime.Clock
import org.w3c.dom.Worker

actual class CountDownTimer actual constructor(
    actual var totalMillis: Long,
    actual val onTick: (Long) -> Unit,
    actual val onFinish: () -> Unit,
    actual val isRunning: (Boolean) -> Unit,
) {
    actual var currentMillis: Long = totalMillis
    private val initalTotalMillis = totalMillis
    init {
        isRunning.invoke(false)
       // document.title = (currentMillis / 1000).toInt().toString()
    }

    private var worker: Worker? = null
    private var startMoment: Long = 0

    actual fun startTimer() {
        isRunning.invoke(true)
        startMoment = Clock.System.now().toEpochMilliseconds()
        worker = Worker(
            "data:text/javascript," +
                    "onmessage = function (e) {\n" +
                    "    let count = setInterval(function () {\n" +
                    "        postMessage(null);\n" +
                    "    }, 1000);\n" +
                    "}"
        )
        worker?.postMessage(null)
        worker?.onmessage = { onTick() }
    }

    private fun onTick() {
        val timePass =
            ((Clock.System.now()
                .toEpochMilliseconds() - startMoment).toDouble() / 1000).toLong() * 1000
        currentMillis = totalMillis - timePass

        if (currentMillis <= 0) {
            onTick.invoke(0)
            onFinish.invoke()
            isRunning.invoke(false)
            worker?.terminate()
        } else {
            onTick.invoke(currentMillis)
        }
    }

    actual fun pauseTimer() {
        isRunning.invoke(false)
        totalMillis = currentMillis
        worker?.terminate()
    }

    actual fun resetTimer(){
        isRunning.invoke(false)
        totalMillis = initalTotalMillis
        currentMillis = initalTotalMillis
        onTick.invoke(initalTotalMillis)
        worker?.terminate()
    }

    actual fun isFinished() : Boolean{
        return currentMillis == 0L
    }
}