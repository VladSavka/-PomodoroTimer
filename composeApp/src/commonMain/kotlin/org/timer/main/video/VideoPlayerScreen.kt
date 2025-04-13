package org.timer.main.video

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import org.timer.main.settings.*
import org.timer.main.timer.*

@Composable
fun VideoPlayerScreen(
    viewState: TimerViewState,
) {
    val showDialog = remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(top = 64.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Youtube video:",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            AnimatedVisibility(viewState.videoLink != null && !showDialog.value) {
                if (viewState.videoLink != null && !showDialog.value) {
                    VideoPlayer(
                        modifier = Modifier.padding(
                            top = 32.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ).fillMaxWidth().height(300.dp),
                        url = viewState.videoLink,
                    )
                }
            }
        }

        if (showDialog.value) {
            SettingsDialogScreen(isDialogVisible = { showDialog.value = it })
        }


        IconButton(
            onClick = { showDialog.value = true },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }

    }
}