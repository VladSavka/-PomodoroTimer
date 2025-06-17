package org.timer.main.timer

import kotlinx.datetime.*
import org.timer.main.domain.settings.*
import platform.UserNotifications.*

private const val ALARM_ID = "AlarmID"

actual class MobileAlarm actual constructor(actual val context: Any?) {
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    actual fun schedule(
        scheduledDateTimeMillis: Long,
        alarmSound: AlarmSound,
        title: String,
        body: String,
    ) {
        val instant = Instant.fromEpochMilliseconds(scheduledDateTimeMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val triggerDateComponents = localDateTime.toNSDateComponents()

        val soundFileName = when (alarmSound) {
            AlarmSound.CAT -> "cat.wav"
            AlarmSound.BIRD -> "bird.wav"
            AlarmSound.BUFFALO -> "buffalo.wav"
            AlarmSound.DOG -> "dog.wav"
            AlarmSound.WOLF -> "standard.wav"
            AlarmSound.STANDARD -> "wolf.wav"
        }

        val content = UNMutableNotificationContent().apply {
            setUserInfo(mapOf("alarm_id" to ALARM_ID))
            setTitle(title)

            setSound(UNNotificationSound.soundNamed(soundFileName))
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            dateComponents = triggerDateComponents,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = ALARM_ID,
            content = content,
            trigger = trigger
        )

//        notificationCenter.addNotificationRequest(request) { error ->
//            if (error != null) {
//                println("Error scheduling iOS notification for ID $ALARM_ID: ${error.localizedDescription}")
//            } else {
//                println("iOS notification scheduled successfully for ID $ALARM_ID at $localDateTime")
//            }
//        }
    }

    actual fun cancel() {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(ALARM_ID))
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(ALARM_ID))
        println("iOS notifications (pending and delivered) cancelled for ID: $ALARM_ID")
    }


    actual fun startLiveNotification(
        title: String,
        isBreak: Boolean,
        totalTimeLeftMillis: Long,
        alarmSound: AlarmSound
    ) {
        val soundFileName = when (alarmSound) {
            AlarmSound.CAT -> "cat.wav"
            AlarmSound.BIRD -> "bird.wav"
            AlarmSound.BUFFALO -> "buffalo.wav"
            AlarmSound.DOG -> "dog.wav"
            AlarmSound.WOLF -> "standard.wav"
            AlarmSound.STANDARD -> "wolf.wav"
        }

        startLiveActivity.invoke(title, isBreak, totalTimeLeftMillis,soundFileName)
    }

    actual fun stopLiveNotification() {
        cancelLiveActivity.invoke()
    }
}

