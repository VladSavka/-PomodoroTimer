package org.timer.main.projects

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.*
import org.koin.compose.viewmodel.*
import org.timer.main.domain.project.*

@Composable
fun ProjectsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProjectsViewModel = koinViewModel(),
) {

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    var prevSize by remember { mutableStateOf(viewState.projects.size) }

    val lazyListState = rememberLazyListState()
    LaunchedEffect(viewState.projects.size) {
        val currentSize = viewState.projects.size
        if (prevSize > currentSize) {
            prevSize = currentSize
        } else {
            prevSize = currentSize
            lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
        }


    }

    viewState.projects.forEachIndexed { index, project ->
        LaunchedEffect(project.tasks.size) {
            if (lazyListState.layoutInfo.totalItemsCount - 1 == index) {
                lazyListState.animateScrollToItem(index + 1)
            }
        }
    }

    Box(modifier.fillMaxSize()) {
        if (viewState.projects.isEmpty()) {
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                text = "Your project list is empty.\n Add your first project using button below."
            )
        } else {
            Row(Modifier.padding(horizontal = 16.dp)) {
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 36.dp, top = 8.dp),
                    state = lazyListState,
                ) {
                    items(viewState.projects) { task ->
                        ProjectItem(
                            Modifier.animateItem(),
                            task,
                            { viewModel.removeProject(it) },
                            { project, description ->
                                viewModel.onTaskSubmit(
                                    project.id,
                                    description
                                )
                            },
                            { project, task -> viewModel.onTaskDoneClick(project.id, task.id) },
                            { project, task -> viewModel.onTaskUndoneClick(project.id, task.id) }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = {
                viewModel.onProjectTitleUpdate("")
                showDialog = true
            },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Filled.Add, "Add Project")
        }
    }

    if (showDialog) {
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        AlertDialog(
            title = { Text(text = "Add Project") },
            text = {
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    value = viewState.projectName,
                    onValueChange = { viewModel.onProjectTitleUpdate(it) },
                    label = { Text("Name") }
                )
            },
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        viewModel.onAddProjectSubmitClick()
                    },
                    enabled = viewState.isSubmitEnabled
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun ProjectItem(
    modifier: Modifier,
    project: Project,
    onDeleteClick: (Long) -> Unit,
    onAddTaskClick: (Project, String) -> Unit,
    onTaskDoneClick: (Project, Task) -> Unit,
    onTaskUndoneClick: (Project, Task) -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Header(project, { onDeleteClick(project.id) })
            HorizontalDivider()
            project.tasks.forEach { task ->
                TaskItem(
                    task,
                    { onTaskDoneClick.invoke(project, it) },
                    { onTaskUndoneClick.invoke(project, it) })
                HorizontalDivider()
            }
            HorizontalDivider()
            AddTaskFooter { onAddTaskClick(project, it) }
        }
    }
}

@Composable
fun AddTaskFooter(onAddTaskClick: (String) -> Unit) {
    var taskName by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Add Task") },
            modifier = Modifier.weight(1f)
                .padding(start = 0.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )
        IconButton(
            modifier = Modifier.size(20.dp),
            onClick = {
                if (taskName.isNotBlank()) {
                    onAddTaskClick(taskName)
                    taskName = "" // Clear the text field after submission
                }
            },
            enabled = taskName.isNotBlank()
        ) {
            Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = "Submit Task")
        }
    }
}

@Composable
fun TaskItem(task: Task, onTaskDoneClick: (Task) -> Unit, onTaskUndoneClick: (Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp).alpha(if (task.isDone) 0.5f else 1f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    onTaskDoneClick(task)
                } else {
                    onTaskUndoneClick(task)
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (task.isDone) {
            Text(
                text = task.description,
                modifier = Modifier.weight(1f),
                style = LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough),
            )
        } else {
            Text(
                text = task.description,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Header(
    project: Project,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = project.title,
            modifier = Modifier
                .padding(start = 0.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
                .weight(1f),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Box(modifier = Modifier) {
            IconButton(
                modifier = Modifier.size(20.dp).align(Alignment.TopEnd)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(1.dp)
                    .clip(CircleShape),

                onClick = onCloseClick
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close")
            }
        }
    }
}