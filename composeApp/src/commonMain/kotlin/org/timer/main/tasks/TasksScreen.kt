package org.timer.main.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diamondedge.logging.logging
import org.timer.main.data.Task

@Composable
fun TasksScreen(
    modifier: Modifier,
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
    logging().debug { "TaskItem " + task.title }
    Text(task.title)
}
