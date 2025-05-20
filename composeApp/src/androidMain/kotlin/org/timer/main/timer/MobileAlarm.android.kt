package org.timer.main.timer

import android.app.*
import android.content.*
import com.tweener.alarmee.*
import com.tweener.alarmee.channel.*
import com.tweener.alarmee.configuration.*
import kotlinx.datetime.*
import org.jetbrains.compose.resources.*
import org.timer.main.domain.settings.*
import pomodorotimer.composeapp.generated.resources.*
import pomodorotimer.composeapp.generated.resources.Res

actual class MobileAlarm actual constructor(actual val context: Any?) {
    @OptIn(ExperimentalResourceApi::class)
    val platformConfiguration: AlarmeeAndroidPlatformConfiguration =
        AlarmeeAndroidPlatformConfiguration(
            notificationIconResId = com.tweener.alarmee.android.R.drawable.ic_notification,
            notificationIconColor = androidx.compose.ui.graphics.Color.Red, // Defaults to Color.Transparent is not specified
            notificationChannels = listOf(
                AlarmeeNotificationChannel(
                    id = "dailyNewsChannelId",
                    name = "Daily news notifications",
                    importance = NotificationManager.IMPORTANCE_HIGH,
                ),
            )

        )
    private val alarmeeScheduler: AlarmeeScheduler =
        AlarmeeSchedulerAndroid(context as Context, platformConfiguration)


    @OptIn(ExperimentalResourceApi::class)
    actual fun schedule(
        scheduledDateTimeMillis: Long,
        alarmSound: AlarmSound
    ) {
        val instant = Instant.fromEpochMilliseconds(scheduledDateTimeMillis)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        alarmeeScheduler
        alarmeeScheduler.schedule(
            alarmee = Alarmee(
                uuid = "KittidoroAlarmId",
                notificationTitle = "🎉 Congratulations! You've scheduled an Alarmee!",
                notificationBody = "This is the notification that will be displayed at the specified date and time.",
                scheduledDateTime = localDateTime,
                androidNotificationConfiguration = AndroidNotificationConfiguration(
                    // Required configuration for Android target only (this parameter is ignored on iOS)
                    priority = AndroidNotificationPriority.HIGH,
                    channelId = "dailyNewsChannelId",
                ),
                iosNotificationConfiguration = IosNotificationConfiguration(
                   // soundFilename = "cat.wav",
                ),
            )
        )
    }

    actual fun cancel() {
        alarmeeScheduler.cancel(uuid = "KittidoroAlarmId")
    }
}