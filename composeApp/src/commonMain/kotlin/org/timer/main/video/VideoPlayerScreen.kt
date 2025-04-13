package org.timer.main.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.timer.main.settings.SettingsDialogScreen
import org.timer.main.timer.TimerViewState

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