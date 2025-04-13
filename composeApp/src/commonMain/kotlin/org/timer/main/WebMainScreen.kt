package org.timer.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import androidx.lifecycle.viewmodel.compose.*
import org.timer.main.tasks.*
import org.timer.main.timer.*
import org.timer.main.video.*

@ExperimentalMaterial3Api
@Composable
fun WebMainScreen(
    viewModel: TimerViewModel = viewModel { TimerViewModel() },
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
                  viewModel,
                )
                TasksScreen(
                    modifier = Modifier
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
            val viewState by viewModel.viewState.collectAsStateWithLifecycle()
            VideoPlayerScreen(viewState)
        }
    }
}