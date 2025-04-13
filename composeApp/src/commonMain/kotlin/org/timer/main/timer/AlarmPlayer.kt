package org.timer.main.timer

expect class AlarmPlayer(
) {
    fun  play(onEnded: () -> Unit = {})
    fun cancel()
}