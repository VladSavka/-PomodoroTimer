package org.timer

import androidx.compose.ui.window.*

fun MainViewController() = ComposeUIViewController(configure = { initializeKoin() }) { App() }