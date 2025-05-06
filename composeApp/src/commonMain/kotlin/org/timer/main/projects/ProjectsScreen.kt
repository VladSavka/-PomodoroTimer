package org.timer.main.projects

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
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
                                },
                                onSubmitEditProjectName = { id, name ->
                                    viewModel.onSubmitEditProjectName(id, name)
                                },
                                onSubmitEditTaskDescription = { projectId, taskId, name ->
                                    viewModel.onSubmitEditTaskDescription(projectId, taskId, name)
                                },
                                onDeleteTaskClick = { projectId, taskId ->
                                    viewModel.onDeleteTaskClick(projectId, taskId)
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
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
    project: PresentableProject,
    isDrugging: Boolean,
    onDeleteClick: (Long) -> Unit,
    onSubmitTaskClick: (PresentableProject, String) -> Unit,
    onTaskDoneClick: (PresentableProject, Task) -> Unit,
    onTaskUndoneClick: (PresentableProject, Task) -> Unit,
    onTasksReorder: (PresentableProject, Int, Int) -> Unit,
    onSubmitEditProjectName: (Long, String) -> Unit,
    onSubmitEditTaskDescription: (Long, Long, String) -> Unit,
    onDeleteTaskClick:(Long, Long) -> Unit,
    ) {
    var showAddTaskFooter by remember { mutableStateOf(project.tasks.isEmpty()) }
    var hasRequestedFocus by remember { mutableStateOf(true) } // New state variable
    val focusRequester = remember { FocusRequester() }
    val elevation by animateDpAsState(if (isDrugging) 6.dp else 2.dp)

    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
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
        val dragDropState = rememberDragDropState(lazyListState) { fromIndex, toIndex ->
            onTasksReorder.invoke(project, fromIndex, toIndex)
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Header(
                project = project,
                onDeletProjectClick = { onDeleteClick(project.id) },
                onAddTaskClick = {
                    showAddTaskFooter = true
                    hasRequestedFocus = false // Reset the flag when the footer is shown
                },
                onSubmitEditProjectName = onSubmitEditProjectName
            )
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier
                    .dragContainer(dragDropState)
                    .wrapContentHeight()
                    .heightIn(max = 5000.dp),
                state = lazyListState,
            ) {
                itemsIndexed(project.tasks, key = { _, item -> item.id }) { index, item ->
                    DraggableItem(dragDropState, index) { isDragging ->
                        TaskItem(
                            item,
                            isDragging,
                            { onTaskDoneClick.invoke(project, it) },
                            { onTaskUndoneClick.invoke(project, it) },
                            { taskId, desc ->
                                onSubmitEditTaskDescription(project.id, taskId, desc)
                            },
                            { taskId ->
                                onDeleteTaskClick(project.id, taskId)
                            } )
                    }
                }
            }
            if (showAddTaskFooter) {
                HorizontalDivider()
                AddTaskFooter(
                    focusRequester = focusRequester,
                    onSubmitTaskClick = {
                        onSubmitTaskClick(project, it)
                        showAddTaskFooter = false
                    },
                    onLostFocus = {
                        showAddTaskFooter = false
                    }
                )
                LaunchedEffect(showAddTaskFooter) {
                    if (showAddTaskFooter && !hasRequestedFocus) {
                        focusRequester.requestFocus()
                        hasRequestedFocus = true // Set the flag after requesting focus
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskFooter(
    focusRequester: FocusRequester,
    onSubmitTaskClick: (String) -> Unit,
    onLostFocus: () -> Unit
) {
    var taskName by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    var isSubmitButtonClicked by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isSubmitButtonClicked) {
        if (isSubmitButtonClicked) {
            onLostFocus()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Add Task") },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (taskName.isNotBlank()) {
                        onSubmitTaskClick(taskName)
                        taskName = ""
                    }
                    focusManager.clearFocus()
                    onLostFocus()
                }
            ),
        )
        Button(
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                isSubmitButtonClicked = true
                if (taskName.isNotBlank()) {
                    onSubmitTaskClick(taskName)
                    taskName = ""
                }
                focusManager.clearFocus()
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
    onTaskUndoneClick: (Task) -> Unit,
    onSubmitEditTaskDescription: (Long, String) -> Unit,
    onDeleteTaskClick: (Long) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var shouldSubmit by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(task.description, TextRange(task.description.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val elevation by animateDpAsState(if (isDrugging) 2.dp else 0.dp)

    Column(modifier = Modifier.shadow(elevation = elevation).onFocusChanged {
    }) {
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
            if (isEditing) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .background(Color.Transparent)
                            .onFocusChanged { focusState ->
                                if (isFocused != focusState.isFocused) {
                                    isFocused = focusState.isFocused
                                    if (!focusState.isFocused && shouldSubmit) {
                                        onSubmitEditTaskDescription(task.id, textFieldValue.text)
                                        isEditing = false
                                    }
                                }
                                shouldSubmit = true
                            },
                        textStyle = if (task.isDone) LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough) else LocalTextStyle.current,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onSubmitEditTaskDescription(task.id, textFieldValue.text)
                                isEditing = false
                                keyboardController?.hide()
                            }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        visualTransformation = VisualTransformation.None
                    )
                }
                LaunchedEffect(isEditing) {
                    if (isEditing) {
                        focusRequester.requestFocus()
                    }
                }
            } else {
                Text(
                    text = task.description,
                    modifier = Modifier.weight(1f),
                    style = if (task.isDone) LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough) else LocalTextStyle.current,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box {
                IconButton(
                    modifier = Modifier
                        .size(20.dp),
                    onClick = { isMenuExpanded = true }
                ) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            isEditing = true
                            textFieldValue =
                                TextFieldValue(task.description, TextRange(task.description.length))
                            isMenuExpanded = false
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDeleteTaskClick(task.id)
                            isMenuExpanded = false
                        }
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun Header(
    project: PresentableProject,
    onDeletProjectClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onSubmitEditProjectName: (Long, String) -> Unit,
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(project.name, TextRange(project.name.length)))
    }
    val focusRequester = remember { FocusRequester() }
    var isEditing by remember { mutableStateOf(false) }
    var shouldSubmit by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 32.dp, end = 16.dp, bottom = 8.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(4.dp)
                    )
            ) {
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .background(Color.Transparent)
                        .onFocusChanged { focusState ->
                            if (isFocused != focusState.isFocused) {
                                isFocused = focusState.isFocused
                                if (!focusState.isFocused && shouldSubmit) {
                                    onSubmitEditProjectName(project.id, textFieldValue.text)
                                    isEditing = false
                                }
                            }
                            shouldSubmit = true
                        },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onSubmitEditProjectName(project.id, textFieldValue.text)
                            isEditing = false
                            keyboardController?.hide()
                        }
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
                )
            }
            LaunchedEffect(isEditing) {
                if (isEditing) {
                    focusRequester.requestFocus()
                }
            }
        } else {
            Text(
                text = project.name,
                modifier = Modifier
                    .padding(start = 32.dp, end = 16.dp, top = 0.dp, bottom = 8.dp)
                    .weight(1f),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Box {
                IconButton(
                    modifier = Modifier
                        .size(20.dp),
                    onClick = { isMenuExpanded = true }
                ) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "More")
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit name") },
                        onClick = {
                            isEditing = true
                            textFieldValue =
                                TextFieldValue(project.name, TextRange(project.name.length))
                            isMenuExpanded = false
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDeletProjectClick()
                            isMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}