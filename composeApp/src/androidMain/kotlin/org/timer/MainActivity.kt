package org.timer

import android.content.*
import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // You might do something here if the app is brought to front via notification,
        // even if no specific data is passed.
        // For example, refresh some data or UI element.
        setIntent(intent) // Good practice
    }
}


@Preview
@Composable
fun AppAndroidPreview() {
    App()
}