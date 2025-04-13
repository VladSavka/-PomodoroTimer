package org.timer.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.diamondedge.logging.logging

@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    windowInfo: WindowInfo = remeberWindowInfo()
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        logging().debug { "windowInfo.size.width ${windowInfo.size.width}" }
        if (windowInfo.isMobileDevice()) {
            MobileMainScreen()
        } else {
            WebMainScreen()
        }
    }
}












