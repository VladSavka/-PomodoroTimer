package org.timer.main.timer

import kotlinx.datetime.*
import org.timer.main.domain.settings.*
import platform.UserNotifications.*

private const val ALARM_ID = "KittidoroAlarmID"

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
            setBody(body)
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

        notificationCenter.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error scheduling iOS notification for ID $ALARM_ID: ${error.localizedDescription}")
            } else {
                println("iOS notification scheduled successfully for ID $ALARM_ID at $localDateTime")
            }
        }
    }

    actual fun cancel() {
        notificationCenter.removePendingNotificationRequestsWithIdentifiers(listOf(ALARM_ID))
        notificationCenter.removeDeliveredNotificationsWithIdentifiers(listOf(ALARM_ID))
        println("iOS notifications (pending and delivered) cancelled for ID: $ALARM_ID")
    }

    actual fun showLiveActivity(totalTimeLeftMillis: Long) {
        println("KMP MobileAlarm: Attempting to show Live Activity.")
        // For this example, we'll convert totalTimeLeftMillis to a simple emoji or string.
        // You'll want more sophisticated logic here based on your app's needs.
        val minutes = totalTimeLeftMillis / 1000 / 60
        val emoji = if (minutes > 10) "⏳" else if (minutes > 0) "⏱️" else "🎉"
        val activityName = "Pomodoro Session" // Or make this configurable

//        // Call the Swift bridge
//        GeneratedLiveActivityBridge.shared.startActivity(
//            activityName = activityName,
////            initialEmoji = emoji
//        )
        startLiveActivity(totalTimeLeftMillis)
        println("KMP MobileAlarm: Called Swift to start Live Activity with name '$activityName' and emoji '$emoji'.")
    }
}

