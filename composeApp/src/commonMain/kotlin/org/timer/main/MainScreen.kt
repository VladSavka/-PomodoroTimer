package org.timer.main


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.timer.main.tasks.TasksScreen
import org.timer.main.timer.TimerScreen
import org.timer.main.timer.TimerViewModel
import org.timer.main.video.VideoPlayerScreen

@ExperimentalMaterial3Api
@Composable
fun MainScreen(
    viewModel: TimerViewModel = viewModel { TimerViewModel() },
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)

    ) {
        Row {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Column {
                    TimerScreen(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .fillMaxWidth(),
                        viewState, viewModel,
                    )
                    TasksScreen(modifier = Modifier
                        .fillMaxHeight(1f)
                        .fillMaxWidth()
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight()
                    .padding(start = 4.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            ) {
                VideoPlayerScreen(viewState)
            }
        }
    }
}






