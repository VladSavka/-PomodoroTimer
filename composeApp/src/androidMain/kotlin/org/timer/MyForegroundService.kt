package org.timer

import android.R
import android.app.*
import android.content.*
import android.content.pm.*
import android.os.*
import android.util.*
import androidx.core.app.*
import java.util.*
import java.util.concurrent.*

class MyForegroundService : Service() {

    private var notificationManager: NotificationManagerCompat? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationId = 100

    private var countDownTimer: CountDownTimer? = null
    private var totalDurationMillis: Long = 0
    private var timeLeftInMillis: Long = 0

    // Define a constant for the notification channel ID
    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannelId"
        const val ACTION_START = "org.timer.actions.START"
        const val ACTION_STOP = "org.timer.actions.STOP"
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service Channel", // More descriptive name
                NotificationManager.IMPORTANCE_LOW // Use LOW to avoid sound/vibration by default
            )
            channel.description = "Channel for active timer foreground service"
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                try {
                    val title = intent.getStringExtra("title") ?: "Timer Active"
                    val newTotalTimeLeftMillis = intent.getLongExtra("totalTimeLeftMillis", 0)

                    if (newTotalTimeLeftMillis <= 0) {
                        Log.w(
                            "MyForegroundService",
                            "Invalid totalTimeLeftMillis: $newTotalTimeLeftMillis. Stopping."
                        )
                        stopService()
                        return START_NOT_STICKY
                    }

                    if (countDownTimer != null) {
                        Log.d(
                            "MyForegroundService",
                            "Existing timer found. Stopping it before starting new one."
                        )
                        stopTimer()
                    }

                    totalDurationMillis = newTotalTimeLeftMillis
                    timeLeftInMillis = totalDurationMillis

                    notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_lock_idle_alarm)
                        .setContentTitle(title)
                        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                        .setProgress(
                            totalDurationMillis.toInt(),
                            (totalDurationMillis - timeLeftInMillis).toInt(),
                            false
                        )

                    val notification = notificationBuilder!!.build()

                    ServiceCompat.startForeground(
                        this,
                        notificationId,
                        notification,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE // Adjust if a more specific type applies
                        } else {
                            0
                        }
                    )
                    Log.d(
                        "MyForegroundService",
                        "Service started with countdown from $totalDurationMillis ms"
                    )
                    startTimer(title, totalDurationMillis)
                } catch (e: Exception) {
                    Log.e("MyForegroundService", "onStartCommand Failed: ${e.message}", e)
                    stopService() // Stop service on error
                }
            }

            ACTION_STOP -> {
                Log.d("MyForegroundService", "Service stopping via STOP action.")
                stopService()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer(title: String, durationMillis: Long) {
        countDownTimer?.cancel() // Cancel any existing timer

        countDownTimer = object : CountDownTimer(durationMillis, 1000) { // Tick every 1 second
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateNotification(title, false)
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateNotification(title, isFinished = true)
                Log.d("MyForegroundService", "Timer finished.")
                stopService()
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        timeLeftInMillis = 0 // Reset time left
    }

    private fun updateNotification(title: String, isFinished: Boolean = false) {
        notificationBuilder?.let { builder ->
            val currentProgress = (totalDurationMillis - timeLeftInMillis).toInt()
            builder.setProgress(totalDurationMillis.toInt(), currentProgress, false)
            builder.setContentTitle(title + ": " + formatTimeLeft(timeLeftInMillis))

            if (isFinished) {
                // Optionally change title or text when finished
                builder.setContentTitle("Timer Finished!")
                // Remove progress bar or set it to max
                builder.setProgress(0, 0, false) // Hides progress bar
                // builder.setProgress(totalDurationMillis.toInt(), totalDurationMillis.toInt(), false) // Shows full
            }
            try {
                notificationManager?.notify(notificationId, builder.build())
            } catch (ex: Exception) {
                Log.e("MyForegroundService", "Error updating notification: ${ex.message}", ex)
            }
        }
    }

    private fun formatTimeLeft(millis: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(millis)

        return if (hours > 0) {
            String.format(Locale.getDefault(),"%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
        }
    }

    private fun stopService() {
        Log.d("MyForegroundService", "Stopping service and foreground notification.")
        stopTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
    }
}