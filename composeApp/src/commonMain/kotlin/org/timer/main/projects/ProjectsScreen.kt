package org.timer.main.projects

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.foundation.text.selection.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
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


    val dragDropState =
        rememberDragDropState(lazyListState) { fromIndex, toIndex ->
            viewModel.onProjectsDrugAndDrop(fromIndex, toIndex)
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
                    modifier = Modifier.dragContainer(dragDropState),
                    contentPadding = PaddingValues(bottom = 36.dp, top = 8.dp),
                    state = lazyListState,
                ) {
                    itemsIndexed(viewState.projects, key = { _, item -> item.id }) { index, item ->
                        DraggableItem(dragDropState, index) { isDragging ->
                            ProjectItem(
                                modifier = Modifier.animateItem(),
                                project = item,
                                isDrugging = isDragging,
                                onDeleteClick = { viewModel.removeProject(it) },
                                onSubmitTaskClick = { project, description ->
                                    viewModel.onTaskSubmitClick(
                                        project.id,
                                        description
                                    )
                                },
                                onTaskDoneClick = { project, task ->
                                    viewModel.onTaskDoneClick(
                                        project.id,
                                        task.id
                                    )
                                },
                                onTaskUndoneClick = { project, task ->
                                    viewModel.onTaskUndoneClick(
                                        project.id,
                                        task.id
                                    )
                                },
                                onTasksReorder = { project, fromIndex, toIndex ->
                                    viewModel.onTasksDrugAndDrop(project.id, fromIndex, toIndex)
                                }
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                        }
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
    isDrugging: Boolean,
    onDeleteClick: (Long) -> Unit,
    onSubmitTaskClick: (Project, String) -> Unit,
    onTaskDoneClick: (Project, Task) -> Unit,
    onTaskUndoneClick: (Project, Task) -> Unit,
    onTasksReorder: (Project, Int, Int) -> Unit,
) {

    var showAddTaskFooter by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val elevation by animateDpAsState(if (isDrugging) 6.dp else 2.dp)

    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
        modifier = modifier.fillMaxWidth().wrapContentHeight().shadow(
            elevation = elevation,
            shape = CardDefaults.shape,
        )
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (showAddTaskFooter) {
                        showAddTaskFooter = false
                    }
                })
            }
    ) {

        val lazyListState = rememberLazyListState()
        val dragDropState =
            rememberDragDropState(lazyListState) { fromIndex, toIndex ->
                onTasksReorder.invoke(project, fromIndex, toIndex)
            }
        Column(modifier = Modifier.padding(16.dp)) {
            Header(
                project = project,
                onCloseProjectClick = { onDeleteClick(project.id) },
                onAddTaskClick = { showAddTaskFooter = true },
                onEditProjectClick = { }
            )
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier.dragContainer(dragDropState).wrapContentHeight()
                    .heightIn(max = 1000.dp),
                state = lazyListState,
            ) {
                itemsIndexed(project.tasks, key = { _, item -> item.id }) { index, item ->
                    DraggableItem(dragDropState, index) { isDragging ->
                        TaskItem(
                            item,
                            isDragging,
                            { onTaskDoneClick.invoke(project, it) },
                            { onTaskUndoneClick.invoke(project, it) })
                    }
                }
            }
            if (showAddTaskFooter) {
                HorizontalDivider()
                AddTaskFooter(focusRequester) {
                    onSubmitTaskClick(project, it)
                    showAddTaskFooter = false
                }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }

        }
    }
}

@Composable
fun AddTaskFooter(focusRequester: FocusRequester, onSubmitTaskClick: (String) -> Unit) {
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
            modifier = Modifier.weight(1f).focusRequester(focusRequester)
                .padding(start = 0.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .onKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                        if (taskName.isNotBlank()) {
                            onSubmitTaskClick(taskName)
                            taskName = "" // Clear the text field after submission
                        }
                        true
                    } else {
                        false
                    }
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                }
            ),
            singleLine = true,
        )
        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                if (taskName.isNotBlank()) {
                    onSubmitTaskClick(taskName)
                    taskName = "" // Clear the text field after submission
                }
            },
            enabled = taskName.isNotBlank()
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    isDrugging: Boolean,
    onTaskDoneClick: (Task) -> Unit,
    onTaskUndoneClick: (Task) -> Unit
) {
    val elevation by animateDpAsState(if (isDrugging) 2.dp else 0.dp)
    Column(modifier = Modifier.shadow(elevation = elevation)) {
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
        HorizontalDivider()
    }
}

@Composable
private fun Header(
    project: Project,
    onCloseProjectClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onEditProjectClick: (String) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(project.title, TextRange(project.title.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp, bottom = 8.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(4.dp)
                    )
            ) {
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .background(Color.Transparent),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onEditProjectClick(textFieldValue.text)
                                isEditing = false
                                keyboardController?.hide()
                            }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        visualTransformation = VisualTransformation.None
                    )
                }
            }
            LaunchedEffect(isEditing) {
                if (isEditing) {
                    focusRequester.requestFocus()
                }
            }
        } else {
            Text(
                text = project.title,
                modifier = Modifier
                    .padding(start = 0.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
                    .weight(1f),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Row(
            modifier = Modifier.wrapContentWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(2.dp)
                    .clip(CircleShape),
                onClick = {
                    isEditing = true
                    textFieldValue = TextFieldValue(project.title, TextRange(project.title.length))
                }
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = "Edit")
            }
            IconButton(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(1.dp)
                    .clip(CircleShape),
                onClick = onAddTaskClick
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
            IconButton(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(1.dp)
                    .clip(CircleShape),
                onClick = onCloseProjectClick
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close")
            }
        }
    }
}