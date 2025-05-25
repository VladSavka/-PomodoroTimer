package org.timer.main.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import org.timer.*
import org.timer.main.domain.settings.AlarmSound // Your KMP AlarmSound enum/class

actual class MobileAlarm actual constructor(actual val context: Any?) {

    private val androidContext = context as? Context
    private val alarmManager = androidContext?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    actual fun schedule(
        scheduledDateTimeMillis: Long,
        alarmSound: AlarmSound,
        title: String,
        body: String
    ) {
        if (alarmManager == null) {
            Log.e("MobileAlarm", "AlarmManager is null, cannot schedule.")
            return
        }

        // Check for exact alarm permission on Android 12 (API 31) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("MobileAlarm", "SCHEDULE_EXACT_ALARM permission not granted. Cannot schedule exact alarm.")
                // IMPORTANT: You should ideally handle this in your UI before calling schedule.
                // The UI should check canScheduleExactAlarms() and if false,
                // prompt the user and provide a button to open settings:
                 openExactAlarmSettings()
                // For this class, we will just log and return.
                // Alternatively, you could throw an exception or have a callback to notify the caller.
                return
            }
        }

        val intent = Intent(androidContext, AlarmReceiver::class.java).apply {
            // Add extras for the BroadcastReceiver
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_BODY, body)
            putExtra(EXTRA_ALARM_SOUND_NAME, alarmSound.name) // Pass enum name
        }

        // Use FLAG_IMMUTABLE as it's required for PendingIntents targeting Android 12+
        // Use FLAG_UPDATE_CURRENT to ensure if an alarm with the same request code is set,
        // its extras are updated.
        val pendingIntent = PendingIntent.getBroadcast(
            androidContext,
            ALARM_REQUEST_CODE, // A unique int for this alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // AlarmManager.AlarmClockInfo is used for alarms that should be treated like
            // a user-set alarm clock (e.g., visible on lock screen, may bypass some Doze restrictions).
            // The second parameter to AlarmClockInfo is a PendingIntent to show an activity when
            // the user interacts with the alarm icon in the status bar (optional).
            val alarmClockInfo = AlarmManager.AlarmClockInfo(scheduledDateTimeMillis, null)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

            Log.i("MobileAlarm", "Alarm scheduled for $scheduledDateTimeMillis with sound ${alarmSound.name}, title: '$title'")
        } catch (se: SecurityException) {
            // This should ideally not happen if canScheduleExactAlarms() was checked and returned true.
            Log.e("MobileAlarm", "SecurityException while scheduling alarm. This is unexpected.", se)
        } catch (e: Exception) {
            Log.e("MobileAlarm", "Exception while scheduling alarm.", e)
        }
    }

    actual fun cancel() {
        if ( alarmManager == null) {
            Log.w("MobileAlarm", "Cannot cancel alarm: Context or AlarmManager is null.")
            return
        }

        val intent = Intent(androidContext, AlarmReceiver::class.java) // Must match the scheduling intent

        // To cancel, the PendingIntent must match the one used for scheduling.
        // FLAG_NO_CREATE ensures that if the PendingIntent doesn't already exist, a new one isn't created.
        val pendingIntent = PendingIntent.getBroadcast(
            androidContext,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel() // Also cancel the PendingIntent itself to free resources
            Log.i("MobileAlarm", "Alarm cancelled successfully.")
        } else {
            Log.w("MobileAlarm", "Alarm could not be cancelled: PendingIntent not found. Was it scheduled with request code $ALARM_REQUEST_CODE?")
        }
    }


     fun canScheduleExactAlarms(): Boolean {
         if ( alarmManager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
             return true // Permission not restricted before Android S, or context/manager unavailable (treat as allowed for check)
         }
         return alarmManager.canScheduleExactAlarms()
     }

     fun openExactAlarmSettings() {
         if (androidContext == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
         Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also { intent ->
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             if (intent.resolveActivity(androidContext.packageManager) != null) {
                 androidContext.startActivity(intent)
             } else {
                 Log.e("MobileAlarm", "Cannot open settings for SCHEDULE_EXACT_ALARM.")
             }
         }
     }
    actual fun startLiveActivity(totalTimeLeftMillis: Long) {
    }
}