package org.timer

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.jetbrains.compose.resources.*
import org.jetbrains.compose.ui.tooling.preview.*
import org.koin.compose.*
import org.timer.main.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
//    KoinApplication(application = {
//        modules(appModule())
//    }) {
        MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
            MainScreen()
        }
 //   }
}

