package org.timer

import android.app.*
import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.*
import org.timer.main.timer.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ForegroundServiceChannelId",
                "Live Activity Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}


@Preview
@Composable
fun AppAndroidPreview() {
    App()
}