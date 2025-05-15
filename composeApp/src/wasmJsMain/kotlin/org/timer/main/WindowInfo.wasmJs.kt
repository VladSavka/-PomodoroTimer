package org.timer.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import kotlinx.browser.window

@Composable
actual fun remeberWindowInfo(): WindowInfo {
    val width = window.innerWidth.toFloat()
    val height = window.innerHeight.toFloat()
    return remember { WindowInfo(Size(width, height)) }
}

actual fun isWeb() = true
actual fun isIOS(): Boolean =false