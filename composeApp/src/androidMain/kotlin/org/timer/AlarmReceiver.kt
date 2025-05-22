package org.timer

import android.R
import android.app.*
import android.content.*
import android.media.*
import android.net.*
import android.os.*
import androidx.core.app.*
import com.diamondedge.logging.*
import org.timer.main.domain.settings.*

const val ALARM_REQUEST_CODE = 123
const val NOTIFICATION_ID = 1
const val NOTIFICATION_CHANNEL_ID = "timer_alarm_channel"
const val NOTIFICATION_CHANNEL_NAME = "Timer Alarms"

const val EXTRA_TITLE = "org.timer.main.timer.EXTRA_TITLE"
const val EXTRA_BODY = "org.timer.main.timer.EXTRA_BODY"
const val EXTRA_ALARM_SOUND_NAME = "org.timer.main.timer.EXTRA_ALARM_SOUND_NAME"


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Timer Finished"
        val body = intent.getStringExtra(EXTRA_BODY) ?: "Your timer is up!"
        val alarmSoundName = intent.getStringExtra(EXTRA_ALARM_SOUND_NAME)
            ?: throw IllegalArgumentException("Sound not provided")
        val alarmSound = AlarmSound.valueOf(alarmSoundName)

        val soundUri = getAlarmSoundUri(context, alarmSound)
        Log.d("AlarmReceiver", "Received alarm with sound: $soundUri")
        createNotificationChannel(context,soundUri)

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingActivityIntent = PendingIntent.getActivity(
            context, 0, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notificationBuilder = NotificationCompat.Builder(context, soundUri.toString())
            .setSmallIcon(R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingActivityIntent)
            .setAutoCancel(true)
            .setSound(soundUri)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context, alarmSound: Uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                alarmSound.toString(),
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for timer alarms"
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(alarmSound, audioAttributes)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getAlarmSoundUri(context: Context, alarmSound: AlarmSound): Uri {
        return when (alarmSound) {
            AlarmSound.CAT -> Uri.parse("android.resource://${context.packageName}/raw/cat") // Replace with actual sound
            AlarmSound.BIRD -> Uri.parse("android.resource://${context.packageName}/raw/bird") //
            AlarmSound.BUFFALO -> Uri.parse("android.resource://${context.packageName}/raw/buffalo") //
            AlarmSound.DOG -> Uri.parse("android.resource://${context.packageName}/raw/dog") //
            AlarmSound.WOLF -> Uri.parse("android.resource://${context.packageName}/raw/wolf") //
            AlarmSound.STANDARD -> Uri.parse("android.resource://${context.packageName}/raw/standard") //
        }
    }
}