package org.timer.main.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import androidx.lifecycle.viewmodel.compose.*
import org.timer.main.data.*

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel { TasksViewModel() },
) {

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        Row { HorizontalDivider(thickness = 2.dp) }
        Row {
            LazyColumn(Modifier.fillMaxWidth()) {
                items(viewState.tasks) { task ->
                    TaskItem(task)
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task) {
    Text(task.title)
}
