package org.timer

import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.*
import org.timer.main.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        MainScreen()
    }
}

