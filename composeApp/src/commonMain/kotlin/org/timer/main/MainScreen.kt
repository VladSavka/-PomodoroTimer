package org.timer.main


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.diamondedge.logging.*

@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    windowInfo: WindowInfo = remeberWindowInfo()
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        if (windowInfo.isMobileDevice()) {
            MobileMainScreen()
        } else {
            WebMainScreen()
        }
    }
}












